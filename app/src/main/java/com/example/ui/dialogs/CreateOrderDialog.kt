package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DeliveryStatus
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.ui.components.Formatter
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DraftItem(
    val id: Long = System.nanoTime(),
    val productName: String = "",
    val packType: String = "Box",
    val quantityStr: String = "1",
    val unitPriceStr: String = "0"
) {
    val quantity: Int get() = quantityStr.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val unitPrice: Double get() = unitPriceStr.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val isValid: Boolean get() = productName.isNotBlank() && quantity > 0 && unitPrice >= 0.0
    val subtotal: Double get() = if (productName.isNotBlank() && quantity > 0) quantity * unitPrice else 0.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderDialog(
    shops: List<Shop>,
    riders: List<Rider>,
    editingOrder: OrderEntity? = null,
    editingItems: List<OrderItemEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSaveOrder: (OrderEntity, List<OrderItemEntity>) -> Unit
) {
    val now = System.currentTimeMillis()
    val defaultInvoiceNo = remember { "INV-" + SimpleDateFormat("yyMMdd-HHmm", Locale.US).format(Date(now)) }
    val defaultTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now)) }

    var invoiceNumber by remember { mutableStateOf(editingOrder?.invoiceNumber ?: defaultInvoiceNo) }
    var directInvoiceValueStr by remember { mutableStateOf(if (editingOrder != null) editingOrder.grossAmount.toString() else "") }

    var selectedShop by remember {
        mutableStateOf(
            if (editingOrder != null) shops.find { it.id == editingOrder.shopId } ?: shops.firstOrNull()
            else shops.firstOrNull()
        )
    }
    var shopExpanded by remember { mutableStateOf(false) }

    var customShopName by remember { mutableStateOf(if (editingOrder != null && shops.none { it.id == editingOrder.shopId }) editingOrder.shopName else "") }
    var customShopPhone by remember { mutableStateOf(if (editingOrder != null && shops.none { it.id == editingOrder.shopId }) editingOrder.shopPhone else "") }
    var customShopAddress by remember { mutableStateOf(if (editingOrder != null && shops.none { it.id == editingOrder.shopId }) editingOrder.shopAddress else "") }

    var selectedRider by remember {
        mutableStateOf(
            if (editingOrder != null) riders.find { it.id == editingOrder.riderId } ?: riders.firstOrNull()
            else riders.firstOrNull()
        )
    }
    var riderExpanded by remember { mutableStateOf(false) }

    var assignedTime by remember { mutableStateOf(editingOrder?.assignedTime ?: defaultTime) }
    var discountStr by remember { mutableStateOf(editingOrder?.discount?.toString() ?: "0") }
    var notes by remember { mutableStateOf(editingOrder?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Quick suggestion medicines
    val quickMedicines = listOf("Napa Extra", "Seclo 20", "Maxpro 20", "Ace Plus", "Monas 10", "Fexo 120", "Alatrol")

    // Dynamic Items list (Optional: User can add individual items or leave empty)
    val itemsList = remember {
        val list = mutableStateListOf<DraftItem>()
        if (editingItems.isNotEmpty()) {
            editingItems.forEach { item ->
                list.add(
                    DraftItem(
                        productName = item.productName,
                        packType = item.packType,
                        quantityStr = item.quantity.toString(),
                        unitPriceStr = item.unitPrice.toString()
                    )
                )
            }
        }
        list
    }

    // Calculations: If valid items exist, use item sum; otherwise use direct invoice value
    val validItems = itemsList.filter { it.isValid }
    val hasValidItems = validItems.isNotEmpty()
    val itemsGross = validItems.sumOf { it.subtotal }
    val directInvoiceValue = directInvoiceValueStr.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0

    val totalGross = if (hasValidItems) itemsGross else directInvoiceValue
    val discount = discountStr.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val netPayable = (totalGross - discount).coerceAtLeast(0.0)

    val compactFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = TealPrimary,
        unfocusedBorderColor = Color(0xFFCBD5E1),
        focusedLabelColor = TealPrimary,
        unfocusedLabelColor = Color(0xFF64748B),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF8FAFC)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF8FAFC),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 680.dp)
                .testTag("create_order_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // --- Top Header ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(TealPrimary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (editingOrder != null) "চালান / অর্ডার এডিট করুন" else "নতুন ডেলিভারি চালান",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(TealPrimary, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MEDISALE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "চালান নং ও মূল্য দিয়ে দ্রুত চালান তৈরি",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_create_order_btn")
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                // --- Scrollable Form Content ---
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Top Section: Invoice Number, Direct Invoice Value & Departure Time
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = invoiceNumber,
                                    onValueChange = {
                                        invoiceNumber = it
                                        errorMessage = null
                                    },
                                    label = { Text("চালান নং (Invoice)", fontSize = 11.sp) },
                                    textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                    colors = compactFieldColors,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("input_invoice_number"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = if (hasValidItems) itemsGross.toString() else directInvoiceValueStr,
                                    onValueChange = {
                                        if (!hasValidItems) {
                                            directInvoiceValueStr = it
                                            errorMessage = null
                                        }
                                    },
                                    readOnly = hasValidItems,
                                    label = { Text("চালান মূল্য (৳)*", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    placeholder = { Text("যেমন: 2500", fontSize = 11.sp, color = Color(0xFF94A3B8)) },
                                    textStyle = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasValidItems) TealPrimary else Color(0xFF0F172A)
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = compactFieldColors,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .testTag("input_invoice_value"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = assignedTime,
                                    onValueChange = { assignedTime = it },
                                    label = { Text("রওয়ানা সময়", fontSize = 11.sp) },
                                    textStyle = TextStyle(fontSize = 12.sp),
                                    colors = compactFieldColors,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .testTag("input_assigned_time"),
                                    singleLine = true
                                )
                            }

                            // Helpful info notice
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "প্রোডাক্ট যোগ না করলেও শুধু চালান নং ও চালান মূল্য (৳) দিলেই চালান সংরক্ষণ করা যাবে।",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF1E40AF),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 2. Section: Pharmacy & Rider Selection
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Shop Dropdown
                                Column {
                                    Text(
                                        text = "ফার্মেসি / দোকান নির্বাচন",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = shopExpanded,
                                        onExpandedChange = { shopExpanded = !shopExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedShop?.name ?: "নতুন দোকান / অন্যান্য",
                                            onValueChange = {},
                                            readOnly = true,
                                            textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopExpanded) },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.LocalPharmacy,
                                                    contentDescription = null,
                                                    tint = TealPrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            colors = compactFieldColors,
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                                .testTag("dropdown_select_shop"),
                                            shape = RoundedCornerShape(10.dp)
                                        )

                                        ExposedDropdownMenu(
                                            expanded = shopExpanded,
                                            onDismissRequest = { shopExpanded = false }
                                        ) {
                                            shops.forEach { shop ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Column {
                                                            Text(shop.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                            Text("${shop.area} • ${shop.phone}", fontSize = 11.sp, color = Color.Gray)
                                                        }
                                                    },
                                                    onClick = {
                                                        selectedShop = shop
                                                        shopExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    if (selectedShop != null) {
                                        Text(
                                            text = "ঠিকানা: ${selectedShop?.address} | ফোন: ${selectedShop?.phone}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B),
                                            modifier = Modifier.padding(top = 2.dp, start = 4.dp)
                                        )
                                    }
                                }

                                // Rider Dropdown
                                Column {
                                    Text(
                                        text = "ডেলিভারি ম্যান (Rider)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334155)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    ExposedDropdownMenuBox(
                                        expanded = riderExpanded,
                                        onExpandedChange = { riderExpanded = !riderExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedRider?.name ?: "ডেলিভারি ম্যান সিলেক্ট করুন",
                                            onValueChange = {},
                                            readOnly = true,
                                            textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = riderExpanded) },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = NavySecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            },
                                            colors = compactFieldColors,
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth()
                                                .testTag("dropdown_select_rider"),
                                            shape = RoundedCornerShape(10.dp)
                                        )

                                        ExposedDropdownMenu(
                                            expanded = riderExpanded,
                                            onDismissRequest = { riderExpanded = false }
                                        ) {
                                            riders.forEach { rider ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Column {
                                                            Text(rider.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                            Text("${rider.zone} • ${rider.phone} (${rider.vehicleType})", fontSize = 11.sp, color = Color.Gray)
                                                        }
                                                    },
                                                    onClick = {
                                                        selectedRider = rider
                                                        riderExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Section Header: Medicine Products (Optional)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(TealPrimary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "ঔষধ ও প্রোডাক্ট (${itemsList.size}) - ঐচ্ছিক",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = if (itemsList.isEmpty()) "কোনো আইটেম না দিলেও চালান হবে" else "আইটেম দিলে স্বয়ংক্রিয় হিসাব হবে",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    errorMessage = null
                                    itemsList.add(DraftItem(productName = "", packType = "Box", quantityStr = "1", unitPriceStr = "0"))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("btn_add_medicine_item")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("প্রোডাক্ট যোগ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // If no items are in list, show a clean info state
                    if (itemsList.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "কোনো ঔষধ আইটেম যুক্ত নেই (ঐচ্ছিক)। সরাসরি চালান মূল্য দিয়ে চালান সেভ করতে পারেন।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Medicine Items Cards (When user chooses to add specific products)
                    itemsIndexed(itemsList, key = { _, item -> item.id }) { index, item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isValid) Color.White else Color(0xFFFFFBEB)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item.isValid) Color(0xFFE2E8F0) else Color(0xFFFDE68A)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                // Card Top: Item # badge + Delete Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(TealPrimary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (item.productName.isNotBlank()) item.productName else "ঔষধ #${index + 1}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.productName.isNotBlank()) Color(0xFF0F172A) else Color(0xFF94A3B8)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            errorMessage = null
                                            itemsList.removeAt(index)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Product Name Input
                                OutlinedTextField(
                                    value = item.productName,
                                    onValueChange = {
                                        errorMessage = null
                                        itemsList[index] = item.copy(productName = it)
                                    },
                                    placeholder = { Text("ঔষধের নাম লিখুন (যেমন: Napa Extra, Seclo)", fontSize = 12.sp) },
                                    textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                                    colors = compactFieldColors,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_item_name_$index"),
                                    singleLine = true
                                )

                                // Quick suggestion chips if name is empty
                                if (item.productName.isBlank()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        quickMedicines.take(4).forEach { sampleName ->
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        itemsList[index] = item.copy(productName = sampleName)
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(sampleName, fontSize = 10.sp, color = Color(0xFF475569))
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Triple column: Pack | Qty | Unit Price
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = item.packType,
                                        onValueChange = {
                                            itemsList[index] = item.copy(packType = it)
                                        },
                                        label = { Text("প্যাক/টাইপ", fontSize = 10.sp) },
                                        textStyle = TextStyle(fontSize = 12.sp),
                                        colors = compactFieldColors,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = item.quantityStr,
                                        onValueChange = {
                                            errorMessage = null
                                            itemsList[index] = item.copy(quantityStr = it)
                                        },
                                        label = { Text("পরিমাণ (Qty)", fontSize = 10.sp) },
                                        textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = compactFieldColors,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(0.9f)
                                            .testTag("input_item_qty_$index"),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = item.unitPriceStr,
                                        onValueChange = {
                                            errorMessage = null
                                            itemsList[index] = item.copy(unitPriceStr = it)
                                        },
                                        label = { Text("দর (৳)", fontSize = 10.sp) },
                                        textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = compactFieldColors,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_item_price_$index"),
                                        singleLine = true
                                    )
                                }

                                // Subtotal footer
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, end = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.productName.isBlank()) {
                                        Text("⚠️ নাম না লিখলে হিসাবে আসবে না", fontSize = 10.sp, color = Color(0xFFD97706))
                                    } else {
                                        Text(
                                            text = "${item.quantity} × ${Formatter.formatCurrency(item.unitPrice)}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Text(
                                        text = "সাবটোটাল: ${Formatter.formatCurrency(item.subtotal)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isValid) TealPrimary else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Discount & Notes
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = discountStr,
                                onValueChange = { discountStr = it },
                                label = { Text("ডিসকাউন্ট বাদ (৳)", fontSize = 11.sp) },
                                textStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = compactFieldColors,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_order_discount"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("নোট / নির্দেশ", fontSize = 11.sp) },
                                textStyle = TextStyle(fontSize = 12.sp),
                                colors = compactFieldColors,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.3f),
                                singleLine = true
                            )
                        }
                    }

                    // 6. Compact Bill Summary Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (hasValidItems) "মোট কার্যকর ঔষধ (${validItems.size} টি):" else "চালান মোট মূল্য:",
                                        color = Color(0xFF475569),
                                        fontSize = 12.sp
                                    )
                                    Text(Formatter.formatCurrency(totalGross), fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 12.sp)
                                }
                                if (discount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("ডিসকাউন্ট বাদ:", color = Color(0xFFEF4444), fontSize = 12.sp)
                                        Text("- ${Formatter.formatCurrency(discount)}", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFCBD5E1))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("প্রদেয় সর্বমোট চালান মূল্য:", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 13.sp)
                                    Text(
                                        Formatter.formatCurrency(netPayable),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TealPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Error Message Card
                    if (errorMessage != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = Color(0xFFB91C1C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Bottom Action Buttons ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_create_order")
                    ) {
                        Text("বাতিল", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val activeValidItems = itemsList.filter { it.isValid }
                            val directVal = directInvoiceValueStr.toDoubleOrNull() ?: 0.0
                            val finalGross = if (activeValidItems.isNotEmpty()) activeValidItems.sumOf { it.subtotal } else directVal

                            if (finalGross <= 0.0 && activeValidItems.isEmpty()) {
                                errorMessage = "দয়া করে চালান মূল্য (Invoice Value) প্রদান করুন অথবা প্রোডাক্ট আইটেম যোগ করুন!"
                                return@Button
                            }

                            val targetShop = selectedShop ?: Shop(
                                name = if (customShopName.isNotBlank()) customShopName else "General Pharmacy",
                                phone = customShopPhone,
                                address = customShopAddress,
                                area = "Dhaka"
                            )
                            val targetRider = selectedRider ?: riders.firstOrNull() ?: Rider(
                                name = "Self / Unassigned",
                                phone = "",
                                zone = ""
                            )

                            val orderNet = (finalGross - discount).coerceAtLeast(0.0)

                            val finalInvoiceNum = invoiceNumber.ifBlank { defaultInvoiceNo }
                            val newOrder = OrderEntity(
                                id = editingOrder?.id ?: 0L,
                                invoiceNumber = finalInvoiceNum,
                                shopId = targetShop.id,
                                shopName = targetShop.name,
                                shopPhone = targetShop.phone,
                                shopAddress = targetShop.address,
                                riderId = targetRider.id,
                                riderName = targetRider.name,
                                riderPhone = targetRider.phone,
                                orderDate = editingOrder?.orderDate ?: System.currentTimeMillis(),
                                assignedTime = assignedTime.ifBlank { defaultTime },
                                status = editingOrder?.status ?: DeliveryStatus.DISPATCHED.name,
                                grossAmount = finalGross,
                                returnAmount = editingOrder?.returnAmount ?: 0.0,
                                discount = discount,
                                netAmount = orderNet,
                                paidAmount = editingOrder?.paidAmount ?: 0.0,
                                dueAmount = (orderNet - (editingOrder?.paidAmount ?: 0.0) - (editingOrder?.returnAmount ?: 0.0)).coerceAtLeast(0.0),
                                paymentMethod = editingOrder?.paymentMethod ?: "Cash",
                                notes = notes
                            )

                            val orderItems = if (activeValidItems.isNotEmpty()) {
                                activeValidItems.map {
                                    OrderItemEntity(
                                        orderId = 0,
                                        productName = it.productName.trim(),
                                        packType = it.packType.trim(),
                                        quantity = it.quantity,
                                        unitPrice = it.unitPrice,
                                        totalPrice = it.subtotal,
                                        returnedQuantity = 0,
                                        returnAmount = 0.0,
                                        returnReason = ""
                                    )
                                }
                            } else {
                                listOf(
                                    OrderItemEntity(
                                        orderId = 0,
                                        productName = "চালান সামগ্রী ($finalInvoiceNum)",
                                        packType = "Challan",
                                        quantity = 1,
                                        unitPrice = finalGross,
                                        totalPrice = finalGross,
                                        returnedQuantity = 0,
                                        returnAmount = 0.0,
                                        returnReason = ""
                                    )
                                )
                            }

                            onSaveOrder(newOrder, orderItems)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
                        modifier = Modifier
                            .weight(1.6f)
                            .testTag("btn_save_dispatch_order")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ডেলিভারি চালান কনফার্ম", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
