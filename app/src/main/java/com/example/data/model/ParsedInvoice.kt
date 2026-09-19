package com.example.data.model

data class ParsedInvoice(
    val id: String = java.util.UUID.randomUUID().toString(),
    val invoiceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val orderDate: String = "",
    val subTotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val grandTotal: Double = 0.0,
    val items: List<ParsedInvoiceItem> = emptyList(),
    var selectedRiderId: Long? = null,
    var selectedRiderName: String = "",
    var selectedRiderPhone: String = "",
    var isSelected: Boolean = true
)

data class ParsedInvoiceItem(
    val sl: Int = 0,
    val medicineName: String,
    val unit: String = "Box",
    val mrp: Double = 0.0,
    val quantity: Int = 1,
    val totalPrice: Double = 0.0,
    val discountPercent: String = "0%",
    val discountValue: Double = 0.0,
    val netAmount: Double = 0.0
)
