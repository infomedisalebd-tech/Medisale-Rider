package com.example.util

import android.content.Context
import android.net.Uri
import com.example.data.model.ParsedInvoice
import com.example.data.model.ParsedInvoiceItem
import java.io.BufferedReader
import java.io.InputStreamReader

object InvoiceParserEngine {

    /**
     * Parses raw text containing one or multiple invoices (from PDF, OCR, Image, CSV or clipboard).
     */
    fun parseInvoiceText(rawText: String): List<ParsedInvoice> {
        val cleanText = rawText.trim()
        if (cleanText.isBlank()) return emptyList()

        // Check if text is CSV formatted
        if (isCsvFormat(cleanText)) {
            val csvInvoices = parseCsvFormat(cleanText)
            if (csvInvoices.isNotEmpty()) return csvInvoices
        }

        // Split text into individual invoice chunks
        val invoiceChunks = splitIntoInvoiceChunks(cleanText)
        val result = mutableListOf<ParsedInvoice>()

        for (chunk in invoiceChunks) {
            val parsed = parseSingleInvoiceChunk(chunk)
            if (parsed != null && (parsed.invoiceNumber.isNotBlank() || parsed.grandTotal > 0 || parsed.items.isNotEmpty())) {
                result.add(parsed)
            }
        }

        return result
    }

    /**
     * Splits multi-page / multi-invoice text into individual chunks.
     */
    private fun splitIntoInvoiceChunks(text: String): List<String> {
        val lines = text.lines()
        val chunks = mutableListOf<String>()
        var currentChunk = StringBuilder()

        val invoiceStartPattern = Regex("(?i)(?:Invoice\\s*ID|Invoice\\s*No|Order\\s*Id|Medisale|==Start\\s*of\\s*PDF|==Start\\s*of\\s*OCR)")

        for (line in lines) {
            val isHeader = line.contains("Invoice ID:", ignoreCase = true) ||
                    (line.contains("Medisale", ignoreCase = true) && currentChunk.length > 100) ||
                    line.contains("==Start of PDF==", ignoreCase = true)

            if (isHeader && currentChunk.isNotEmpty() && currentChunk.contains("Grand Total", ignoreCase = true)) {
                chunks.add(currentChunk.toString())
                currentChunk = StringBuilder()
            }
            currentChunk.append(line).append("\n")
        }

        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toString())
        }

        // If no multiple chunks detected by header, test delimiter or return as single chunk
        return if (chunks.size > 1) chunks else listOf(text)
    }

    /**
     * Parses a single invoice chunk extracting customer name, phone, address, items and grand total.
     */
    private fun parseSingleInvoiceChunk(chunk: String): ParsedInvoice? {
        val lines = chunk.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return null

        var invoiceNo = ""
        var customerName = ""
        var phone = ""
        var address = ""
        var orderDate = ""
        var subTotal = 0.0
        var discount = 0.0
        var deliveryCharge = 0.0
        var grandTotal = 0.0

        val items = mutableListOf<ParsedInvoiceItem>()

        // 1. Regex extractions
        val invoiceIdMatch = Regex("(?i)Invoice\\s*ID:\\s*([A-Za-z0-9\\-_#]+)").find(chunk)
        if (invoiceIdMatch != null) {
            invoiceNo = invoiceIdMatch.groupValues[1].trim()
        }

        val orderIdMatch = Regex("(?i)Order\\s*Id:\\s*#?([0-9]+)").find(chunk)
        if (invoiceNo.isBlank() && orderIdMatch != null) {
            invoiceNo = "INV-${orderIdMatch.groupValues[1].trim()}"
        }

        val nameMatch = Regex("(?i)Name:\\s*([^\\n\\r]+)").find(chunk)
        if (nameMatch != null) {
            customerName = nameMatch.groupValues[1].trim()
        }

        val loginPhoneMatch = Regex("(?i)Customer\\s*Login\\s*Phone\\s*Number\\s*\\(?([0-9+]+)\\)?").find(chunk)
        val directPhoneMatch = Regex("(?i)Phone:\\s*([0-9+]+)").find(chunk)
        if (directPhoneMatch != null) {
            phone = directPhoneMatch.groupValues[1].trim()
        } else if (loginPhoneMatch != null) {
            phone = loginPhoneMatch.groupValues[1].trim()
        }

        val addressMatch = Regex("(?i)Delivery\\s*Address:\\s*([^\\n]+(?:\\n[^\\n]+)?)").find(chunk)
        if (addressMatch != null) {
            address = addressMatch.groupValues[1].replace("\n", ", ").trim()
            // Clean out headers if captured
            if (address.contains("Order Info:", ignoreCase = true)) {
                address = address.substringBefore("Order Info:").trim()
            }
        }

        val dateMatch = Regex("(?i)Order\\s*Date:\\s*([^\\n]+)").find(chunk)
        if (dateMatch != null) {
            orderDate = dateMatch.groupValues[1].trim()
        }

        val subTotalMatch = Regex("(?i)SubTotal[\\s\\n]*([0-9.,]+)").find(chunk)
        if (subTotalMatch != null) {
            subTotal = subTotalMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
        }

        val discountMatch = Regex("(?i)(?:Product[\\s\\n]*Discount|Discount[\\s\\n]*Value)[\\s\\n]*-?[\\s\\n]*([0-9.,]+)").find(chunk)
        if (discountMatch != null) {
            discount = discountMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
        }

        val deliveryMatch = Regex("(?i)Delivery(?:[\\s\\n]*Charge)?[\\s\\n]*([0-9.,]+)").find(chunk)
        if (deliveryMatch != null) {
            deliveryCharge = deliveryMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
        }

        val grandTotalMatch = Regex("(?i)Grand[\\s\\n]*Total[\\s\\n]*([0-9.,]+)").find(chunk)
        if (grandTotalMatch != null) {
            grandTotal = grandTotalMatch.groupValues[1].replace(",", "").toDoubleOrNull() ?: 0.0
        }

        // 2. Table row parsing for medicine products
        // Format e.g.: 1 Rosutin 10 Box(30's) 660 1 660.00 14% 92.40 567.60
        var insideTable = false
        for (i in lines.indices) {
            val line = lines[i]
            if (line.contains("SL", ignoreCase = true) && line.contains("Medicine", ignoreCase = true)) {
                insideTable = true
                continue
            }
            if (insideTable && (line.contains("Preferred Delivery", ignoreCase = true) ||
                        line.contains("SubTotal", ignoreCase = true) ||
                        line.contains("You Saved", ignoreCase = true) ||
                        line.contains("Received By", ignoreCase = true))) {
                insideTable = false
                break
            }

            if (insideTable) {
                val item = parseTableRow(line)
                if (item != null) {
                    items.add(item)
                }
            }
        }

        // If subtotal / grandtotal not found via regex, calculate from items
        if (items.isNotEmpty()) {
            if (subTotal == 0.0) {
                subTotal = items.sumOf { it.totalPrice }
            }
            if (discount == 0.0) {
                discount = items.sumOf { it.discountValue }
            }
            if (grandTotal == 0.0) {
                val itemsNet = items.sumOf { it.netAmount }
                grandTotal = if (itemsNet > 0) itemsNet else (subTotal - discount).coerceAtLeast(0.0)
            }
        }

        // Fallbacks
        if (invoiceNo.isBlank()) {
            invoiceNo = "SL-" + (5170 + (System.currentTimeMillis() % 900))
        }
        if (customerName.isBlank()) {
            customerName = "ফার্মেসী গ্রাহক (${invoiceNo})"
        }
        if (address.isBlank()) {
            address = "ঢাকা সিটি"
        }

        return ParsedInvoice(
            invoiceNumber = invoiceNo,
            customerName = customerName,
            customerPhone = phone,
            deliveryAddress = address,
            orderDate = orderDate,
            subTotal = subTotal,
            discount = discount,
            deliveryCharge = deliveryCharge,
            grandTotal = grandTotal,
            items = items
        )
    }

    private fun parseTableRow(line: String): ParsedInvoiceItem? {
        val tokens = line.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return null

        // Check if starts with a number (SL)
        val sl = tokens.first().toIntOrNull() ?: return null
        if (tokens.size < 4) return null

        // Look for quantity and prices from end
        // Last tokens usually: [MRP, Qty, TotalPrice, Discount%, DiscountVal, NetAmount]
        // or [Unit, MRP, Qty, TotalPrice]
        val numTokens = mutableListOf<String>()
        var lastWordIndex = tokens.size - 1

        while (lastWordIndex >= 1) {
            val token = tokens[lastWordIndex]
            if (token.matches(Regex("[0-9.,%]+")) || token.endsWith("%")) {
                numTokens.add(0, token)
                lastWordIndex--
            } else {
                break
            }
        }

        val nameAndUnit = tokens.subList(1, lastWordIndex + 1)
        val medName = if (nameAndUnit.isNotEmpty()) nameAndUnit.joinToString(" ") else "Medicine #$sl"
        val unit = if (nameAndUnit.size > 1 && nameAndUnit.last().contains("Box|Strip|Bottle|ml|tube|pc|'s".toRegex(RegexOption.IGNORE_CASE))) {
            nameAndUnit.last()
        } else {
            "Box"
        }

        var qty = 1
        var mrp = 0.0
        var totalPrice = 0.0
        var discountPercent = "0%"
        var discountVal = 0.0
        var netAmt = 0.0

        if (numTokens.size >= 4) {
            mrp = numTokens[0].replace(",", "").toDoubleOrNull() ?: 0.0
            qty = numTokens[1].toIntOrNull() ?: 1
            totalPrice = numTokens[2].replace(",", "").toDoubleOrNull() ?: (mrp * qty)
            if (numTokens.size >= 6) {
                discountPercent = numTokens[3]
                discountVal = numTokens[4].replace(",", "").toDoubleOrNull() ?: 0.0
                netAmt = numTokens[5].replace(",", "").toDoubleOrNull() ?: (totalPrice - discountVal)
            } else {
                netAmt = totalPrice
            }
        } else if (numTokens.isNotEmpty()) {
            totalPrice = numTokens.last().replace(",", "").toDoubleOrNull() ?: 0.0
            netAmt = totalPrice
            qty = if (numTokens.size > 1) numTokens[0].toIntOrNull() ?: 1 else 1
            mrp = if (qty > 0) totalPrice / qty else totalPrice
        }

        return ParsedInvoiceItem(
            sl = sl,
            medicineName = medName,
            unit = unit,
            mrp = mrp,
            quantity = qty,
            totalPrice = if (totalPrice > 0) totalPrice else mrp * qty,
            discountPercent = discountPercent,
            discountValue = discountVal,
            netAmount = if (netAmt > 0) netAmt else totalPrice
        )
    }

    private fun isCsvFormat(text: String): Boolean {
        val firstLine = text.lines().firstOrNull() ?: return false
        return firstLine.contains(",") || firstLine.contains("\t") || firstLine.contains(";")
    }

    private fun parseCsvFormat(text: String): List<ParsedInvoice> {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        val items = mutableListOf<ParsedInvoiceItem>()
        var invoiceNo = "SL-" + (5170 + (System.currentTimeMillis() % 900))
        var shopName = "মেডিসেল ফার্মেসী"
        var phone = "01700000000"
        var address = "ঢাকা সিটি"

        for (i in 1 until lines.size) {
            val row = lines[i].split("[,\\t;]".toRegex()).map { it.trim() }
            if (row.size >= 3) {
                val medName = row[0]
                val qty = row.getOrNull(1)?.toIntOrNull() ?: 1
                val price = row.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                val unit = row.getOrNull(3) ?: "Box"

                items.add(
                    ParsedInvoiceItem(
                        sl = i,
                        medicineName = medName,
                        unit = unit,
                        mrp = price,
                        quantity = qty,
                        totalPrice = price * qty,
                        netAmount = price * qty
                    )
                )
            }
        }

        if (items.isEmpty()) return emptyList()

        val total = items.sumOf { it.totalPrice }
        return listOf(
            ParsedInvoice(
                invoiceNumber = invoiceNo,
                customerName = shopName,
                customerPhone = phone,
                deliveryAddress = address,
                subTotal = total,
                grandTotal = total,
                items = items
            )
        )
    }

    /**
     * Reads text content from a URI (text/csv or binary stream).
     */
    fun readTextFromUri(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BufferedReader(InputStreamReader(stream)).readText()
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Returns authentic realistic Medisale Invoices matching the user's PDF batch.
     */
    fun getSampleMedisaleBatchInvoices(): List<ParsedInvoice> {
        return listOf(
            ParsedInvoice(
                invoiceNumber = "SL-5180",
                customerName = "Osmani Pharmacy",
                customerPhone = "01797491111",
                deliveryAddress = "Kazi bari road, South banasree, Khilgaon, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 1036.20,
                discount = 160.55,
                deliveryCharge = 0.0,
                grandTotal = 875.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Equra Cream", "1 Pc", 30.2, 1, 30.20, "16.5%", 4.98, 25.22),
                    ParsedInvoiceItem(2, "Montene 4", "1 Box = 2 Strips", 140.0, 1, 140.00, "16.5%", 23.10, 116.90),
                    ParsedInvoiceItem(3, "Nebita 2.5", "Box(50's)", 351.0, 1, 351.00, "15%", 52.65, 298.35),
                    ParsedInvoiceItem(4, "Icykool Max Cream", "25gm tube", 65.0, 1, 65.00, "15.5%", 10.07, 54.93),
                    ParsedInvoiceItem(5, "Windel Plus", "Box(18's)", 450.0, 1, 450.00, "15.5%", 69.75, 380.25)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5181",
                customerName = "Imran Hosen",
                customerPhone = "01330262590",
                deliveryAddress = "111/A/1 North Madartek, Khilgaon, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 8068.52,
                discount = 1472.87,
                deliveryCharge = 0.0,
                grandTotal = 6595.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Rosutin 10", "Box(30's)", 660.0, 1, 660.00, "14%", 92.40, 567.60),
                    ParsedInvoiceItem(2, "Filmet SYRUP", "1bottle", 35.0, 10, 350.00, "15.5%", 54.25, 295.75),
                    ParsedInvoiceItem(3, "Napa Syrup 60ml", "60ml", 35.0, 10, 350.00, "14.5%", 50.75, 299.25),
                    ParsedInvoiceItem(4, "Neurocare POT", "Box(30's)", 300.0, 2, 600.00, "15.5%", 93.00, 507.00),
                    ParsedInvoiceItem(5, "Bet A Cream", "1 pc tube", 44.13, 4, 176.52, "22%", 38.83, 137.69),
                    ParsedInvoiceItem(6, "Bet A Ointment", "1 pc tube", 48.0, 4, 192.00, "22%", 42.24, 149.76),
                    ParsedInvoiceItem(7, "BILASTIN 20 (50's)", "50'S", 800.0, 1, 800.00, "20.5%", 164.00, 636.00),
                    ParsedInvoiceItem(8, "Othera 20 Tab", "Box(60's)", 660.0, 2, 1320.00, "19%", 250.80, 1069.20),
                    ParsedInvoiceItem(9, "Maxpro 20 Tablet", "box(140s)", 980.0, 1, 980.00, "19%", 186.20, 793.80),
                    ParsedInvoiceItem(10, "Neurobest", "Box(60's)", 600.0, 1, 600.00, "19%", 114.00, 486.00)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5182",
                customerName = "ছায়াবীথি ফার্মেসী",
                customerPhone = "01754559975",
                deliveryAddress = "বাসাবো ছায়াবীথি মসজিদের বিপরীতে, ছায়াবীথি রোড, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 892.00,
                discount = 146.09,
                deliveryCharge = 0.0,
                grandTotal = 745.00,
                items = listOf(
                    ParsedInvoiceItem(1, "GlucoLeader Enhance Blue Strip", "25'S", 412.0, 1, 412.00, "17.4%", 71.69, 340.31),
                    ParsedInvoiceItem(2, "Milam 7.5", "Box(40's)", 480.0, 1, 480.00, "15.5%", 74.40, 405.60)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5179",
                customerName = "SA Pharma",
                customerPhone = "01716275145",
                deliveryAddress = "Goran Tempu Stand, Khilgaon, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 1650.00,
                discount = 255.75,
                deliveryCharge = 0.0,
                grandTotal = 1394.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Windel Plus", "Box(18's)", 450.0, 1, 450.00, "15.5%", 69.75, 380.25),
                    ParsedInvoiceItem(2, "Linatab E 5/25", "Box(30's)", 1200.0, 1, 1200.00, "15.5%", 186.00, 1014.00)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5178",
                customerName = "Noor Pharma Malibagh",
                customerPhone = "01339569567",
                deliveryAddress = "মালিবাগ চৌধুরী পাড়া, ডিআইটি রোড, Rampura, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 2976.00,
                discount = 465.78,
                deliveryCharge = 0.0,
                grandTotal = 2510.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Napa 500", "510'S", 612.0, 1, 612.00, "14.5%", 88.74, 523.26),
                    ParsedInvoiceItem(2, "Napa Extra", "264'S", 660.0, 1, 660.00, "14%", 92.40, 567.60),
                    ParsedInvoiceItem(3, "Losectil 20", "1 Box = 12 Strip", 600.0, 1, 600.00, "16%", 96.00, 504.00),
                    ParsedInvoiceItem(4, "Itrazen SB", "Box(30's)", 600.0, 1, 600.00, "18%", 108.00, 492.00),
                    ParsedInvoiceItem(5, "Dialiptin-M 500", "1 Box = 2 Strip", 252.0, 2, 504.00, "16%", 80.64, 423.36)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5177",
                customerName = "Rahim Medicine Corner",
                customerPhone = "01829290507",
                deliveryAddress = "151.wapda rood procim rampura, Rampura, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 2540.00,
                discount = 411.35,
                deliveryCharge = 0.0,
                grandTotal = 2128.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Sergel 20", "Box(100's)", 700.0, 1, 700.00, "15.7%", 109.90, 590.10),
                    ParsedInvoiceItem(2, "Sergel 40", "Box(50's)", 550.0, 1, 550.00, "15.7%", 86.35, 463.65),
                    ParsedInvoiceItem(3, "Maxpro 40-Tablet", "Box(60's)", 540.0, 1, 540.00, "19%", 102.60, 437.40),
                    ParsedInvoiceItem(4, "Tofen Syrup 100ml", "1 Pc", 75.0, 10, 750.00, "15%", 112.50, 637.50)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5176",
                customerName = "New Green Medical Hall",
                customerPhone = "01935841305",
                deliveryAddress = "10Tala market, Main Road, Khilgaon, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 2489.30,
                discount = 400.79,
                deliveryCharge = 0.0,
                grandTotal = 2088.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Etorix 120", "Box(30's)", 450.0, 1, 450.00, "15.5%", 69.75, 380.25),
                    ParsedInvoiceItem(2, "GlucoLeader Red Strip", "25'S", 462.0, 2, 924.00, "15.5%", 143.22, 780.78),
                    ParsedInvoiceItem(3, "Rupadin", "50'S", 600.0, 1, 600.00, "18%", 108.00, 492.00),
                    ParsedInvoiceItem(4, "Afrin Nasal Drop", "1 pc", 70.0, 3, 210.00, "15%", 31.50, 178.50),
                    ParsedInvoiceItem(5, "Mycofree Cream", "Tube 15gm", 100.0, 1, 100.00, "16%", 16.00, 84.00)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5175",
                customerName = "Toha Pharmacy",
                customerPhone = "01776432728",
                deliveryAddress = "Road-8, Cng Stand, Khilgaon, Dhaka",
                orderDate = "Sep 19, 2026",
                subTotal = 1835.00,
                discount = 271.28,
                deliveryCharge = 0.0,
                grandTotal = 1563.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Acifix 20 TAB", "Box(100's)", 700.0, 1, 700.00, "15.5%", 108.50, 591.50),
                    ParsedInvoiceItem(2, "Napa Syrup 60ml", "60ml", 35.0, 5, 175.00, "14.5%", 25.38, 149.62),
                    ParsedInvoiceItem(3, "Hemofix FZ", "Box(60's)", 300.0, 1, 300.00, "15%", 45.00, 255.00),
                    ParsedInvoiceItem(4, "Napa Extra", "264'S", 660.0, 1, 660.00, "14%", 92.40, 567.60)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5174",
                customerName = "WELL PHARMA 2",
                customerPhone = "01318711749",
                deliveryAddress = "ROAD-8, CNG STAND, Khilgaon, Dhaka",
                orderDate = "Sep 18, 2026",
                subTotal = 4059.00,
                discount = 710.71,
                deliveryCharge = 0.0,
                grandTotal = 3348.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Maxpro 40-Tablet", "Box(60's)", 540.0, 1, 540.00, "19%", 102.60, 437.40),
                    ParsedInvoiceItem(2, "Betaloc 25mg", "1 Box = 7 Strip", 152.0, 1, 152.00, "16%", 24.32, 127.68),
                    ParsedInvoiceItem(3, "Betaloc 50mg", "1 Box = 7 Strip", 201.0, 1, 201.00, "16%", 32.16, 168.84),
                    ParsedInvoiceItem(4, "GAVISTOP ORAL SUSPENSION", "200ml", 300.0, 1, 300.00, "18%", 54.00, 246.00),
                    ParsedInvoiceItem(5, "Denvar 50ml", "50 ml bottle", 240.0, 1, 240.00, "21.25%", 51.00, 189.00),
                    ParsedInvoiceItem(6, "Filmet SYRUP", "1bottle", 35.0, 2, 70.00, "15.5%", 10.85, 59.15),
                    ParsedInvoiceItem(7, "Napa 500", "510'S", 612.0, 1, 612.00, "14.5%", 88.74, 523.26),
                    ParsedInvoiceItem(8, "BILASTIN 20 (50's)", "50'S", 800.0, 1, 800.00, "20.5%", 164.00, 636.00)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5173",
                customerName = "Al shepha pharma",
                customerPhone = "01915797179",
                deliveryAddress = "129 wapda road West rampura, Khilgaon, Dhaka",
                orderDate = "Sep 18, 2026",
                subTotal = 1330.00,
                discount = 263.33,
                deliveryCharge = 0.0,
                grandTotal = 1066.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Napa Syrup 60ml", "60ml", 35.0, 5, 175.00, "14.5%", 25.38, 149.62),
                    ParsedInvoiceItem(2, "Alcet SYRUP", "60 ml bottle", 50.0, 2, 100.00, "23.5%", 23.50, 76.50),
                    ParsedInvoiceItem(3, "Tofen Syrup 100ml", "1 Pc", 75.0, 3, 225.00, "15%", 33.75, 191.25),
                    ParsedInvoiceItem(4, "Fenadin 50ml Syrup", "1 Pc", 55.0, 2, 110.00, "17%", 18.70, 91.30),
                    ParsedInvoiceItem(5, "B126", "Box(60's)", 720.0, 1, 720.00, "22.5%", 162.00, 558.00)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5172",
                customerName = "Jononi Pharmacy",
                customerPhone = "01745223059",
                deliveryAddress = "8No CNG stand, Khilgaon, Dhaka",
                orderDate = "Sep 18, 2026",
                subTotal = 812.00,
                discount = 128.74,
                deliveryCharge = 0.0,
                grandTotal = 683.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Pogo Syrup", "1 Bottle (100ml)", 100.0, 2, 200.00, "20%", 40.00, 160.00),
                    ParsedInvoiceItem(2, "Napa 500", "510'S", 612.0, 1, 612.00, "14.5%", 88.74, 523.26)
                )
            ),
            ParsedInvoice(
                invoiceNumber = "SL-5171",
                customerName = "BK BISSASH",
                customerPhone = "01839309749",
                deliveryAddress = "বাগান বাড়ি রোড, বটতলা সংলগ্ন, Khilgaon, Dhaka",
                orderDate = "Sep 18, 2026",
                subTotal = 660.00,
                discount = 125.40,
                deliveryCharge = 0.0,
                grandTotal = 534.00,
                items = listOf(
                    ParsedInvoiceItem(1, "Othera 20 Tab", "Box(60's)", 660.0, 1, 660.00, "19%", 125.40, 534.60)
                )
            )
        )
    }
}
