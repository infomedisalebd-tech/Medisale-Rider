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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DeliveryStatus
import com.example.data.model.OrderItemEntity
import com.example.data.model.OrderWithItems
import com.example.ui.components.Formatter
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReturnItemState(
    val originalItem: OrderItemEntity,
    var returnQtyStr: String,
    var returnReason: String
) {
    val returnQty: Int get() = returnQtyStr.toIntOrNull()?.coerceIn(0, originalItem.quantity) ?: 0
    val returnAmount: Double get() = returnQty * originalItem.unitPrice
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSettlementDialog(
    orderWithItems: OrderWithItems,
    onDismiss: () -> Unit,
    onConfirmSettlement: (
        DeliveryStatus,
        String, // deliveryTime
        Double, // paidAmount
        String, // paymentMethod
        String, // notes
        List<OrderItemEntity> // updatedItems
    ) -> Unit
) {
    val order = orderWithItems.order
    val now = System.currentTimeMillis()
    val currentTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(now)) }

    var selectedStatus by remember {
        mutableStateOf(
            when (order.status) {
                DeliveryStatus.DELIVERED.name -> DeliveryStatus.DELIVERED
                DeliveryStatus.PARTIALLY_RETURNED.name -> DeliveryStatus.PARTIALLY_RETURNED
                DeliveryStatus.RETURNED.name -> DeliveryStatus.RETURNED
                DeliveryStatus.CANCELLED.name -> DeliveryStatus.CANCELLED
                else -> DeliveryStatus.DELIVERED
            }
        )
    }

    var deliveryTime by remember {
        mutableStateOf(order.deliveryTime ?: currentTime)
    }

    val returnItemsStates = remember {
        mutableStateListOf<ReturnItemState>().apply {
            addAll(
                orderWithItems.items.map {
                    ReturnItemState(
                        originalItem = it,
                        returnQtyStr = if (it.returnedQuantity > 0) it.returnedQuantity.toString() else "0",
                        returnReason = it.returnReason.ifBlank { "Short Expiry / মেয়াদ কম" }
                    )
                }
            )
        }
    }

    val totalReturnAmount = returnItemsStates.sumOf { it.returnAmount }
    val calculatedNet = (order.grossAmount - totalReturnAmount - order.discount).coerceAtLeast(0.0)

    var paidAmountStr by remember {
        mutableStateOf(
            if (order.paidAmount > 0) order.paidAmount.toInt().toString()
            else calculatedNet.toInt().toString()
        )
    }

    var paymentMethod by remember { mutableStateOf(order.paymentMethod.ifBlank { "Cash" }) }
    var notes by remember { mutableStateOf(order.notes) }

    val paidAmount = paidAmountStr.toDoubleOrNull() ?: 0.0
    val dueAmount = (calculatedNet - paidAmount).coerceAtLeast(0.0)

    val returnReasonsList = listOf(
        "Short Expiry / মেয়াদ কম",
        "Damaged Packing / ক্ষতিগ্রস্ত",
        "Shop Cancelled / দোকানদার নেয়নি",
        "Wrong Item / ভুল ঔষধ",
        "Excess Stock / অতিরিক্ত স্টক",
        "Customer Refused / কাস্টমার ফেরত দিয়েছে"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF8FAFC),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("order_settlement_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE6F7ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StatusDelivered,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ডেলিভারি ও রিটার্ন হিসাব / Settlement",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${order.invoiceNumber} • ${order.shopName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settlement_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Status Selection Chips
                    item {
                        Column {
                            Text(
                                text = "ডেলিভারি স্ট্যাটাস (Status):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusOptionChip(
                                    label = "ডেলিভার্ড",
                                    selected = selectedStatus == DeliveryStatus.DELIVERED,
                                    color = StatusDelivered,
                                    modifier = Modifier.weight(1f),
                                    onClick = { selectedStatus = DeliveryStatus.DELIVERED }
                                )
                                StatusOptionChip(
                                    label = "আংশিক ফেরত",
                                    selected = selectedStatus == DeliveryStatus.PARTIALLY_RETURNED,
                                    color = StatusPending,
                                    modifier = Modifier.weight(1.2f),
                                    onClick = { selectedStatus = DeliveryStatus.PARTIALLY_RETURNED }
                                )
                                StatusOptionChip(
                                    label = "সম্পূর্ণ ফেরত",
                                    selected = selectedStatus == DeliveryStatus.RETURNED,
                                    color = StatusReturned,
                                    modifier = Modifier.weight(1.2f),
                                    onClick = {
                                        selectedStatus = DeliveryStatus.RETURNED
                                        // Set all returned
                                        returnItemsStates.forEachIndexed { idx, st ->
                                            returnItemsStates[idx] = st.copy(returnQtyStr = st.originalItem.quantity.toString())
                                        }
                                        paidAmountStr = "0"
                                    }
                                )
                                StatusOptionChip(
                                    label = "বাতিল",
                                    selected = selectedStatus == DeliveryStatus.CANCELLED,
                                    color = StatusCancelled,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedStatus = DeliveryStatus.CANCELLED
                                        paidAmountStr = "0"
                                    }
                                )
                            }
                        }
                    }

                    // Delivery Time
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = deliveryTime,
                                onValueChange = { deliveryTime = it },
                                label = { Text("ডেলিভারি সম্পন্ন করার সময় (Delivery Time)") },
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = TealPrimary) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_delivery_time"),
                                singleLine = true
                            )
                            OutlinedButton(
                                onClick = {
                                    deliveryTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_now_time")
                            ) {
                                Text("এখন (Now)", fontSize = 12.sp)
                            }
                        }
                    }

                    // Products List & Returns Input
                    item {
                        Text(
                            text = "প্রোডাক্ট ফেরত / রিটার্ন হিসাব (Return Quantities):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    itemsIndexed(returnItemsStates) { index, returnState ->
                        val item = returnState.originalItem
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (returnState.returnQty > 0) Color(0xFFFFF1F2) else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (returnState.returnQty > 0) borderStroke(1.dp, Color(0xFFFDA4AF)) else null,
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.productName,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "চালান পরিমাণ: ${item.quantity} ${item.packType} • দর: ${Formatter.formatCurrency(item.unitPrice)}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Text(
                                        text = Formatter.formatCurrency(item.totalPrice),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = returnState.returnQtyStr,
                                        onValueChange = {
                                            returnItemsStates[index] = returnState.copy(returnQtyStr = it)
                                            if (selectedStatus == DeliveryStatus.DELIVERED && (it.toIntOrNull() ?: 0) > 0) {
                                                selectedStatus = DeliveryStatus.PARTIALLY_RETURNED
                                            }
                                        },
                                        label = { Text("ফেরত সংখ্যা (Return Qty)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_return_qty_$index"),
                                        singleLine = true
                                    )

                                    if (returnState.returnQty > 0) {
                                        Column(
                                            modifier = Modifier.weight(1.2f),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = "ফেরত মূল্য:",
                                                fontSize = 11.sp,
                                                color = Color(0xFFE11D48)
                                            )
                                            Text(
                                                text = "- ${Formatter.formatCurrency(returnState.returnAmount)}",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFE11D48),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }

                                if (returnState.returnQty > 0) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = returnState.returnReason,
                                        onValueChange = {
                                            returnItemsStates[index] = returnState.copy(returnReason = it)
                                        },
                                        label = { Text("ফেরতের কারণ (Return Reason)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_return_reason_$index"),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }

                    // Auto Calculation Breakdown Box (Gross - Returns - Discount = Net Bill)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "বিল সমন্বয় হিসাব (Auto Calculation):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("মূল চালান মূল্য (Gross):", fontSize = 12.sp, color = Color(0xFF475569))
                                    Text(Formatter.formatCurrency(order.grossAmount), fontWeight = FontWeight.SemiBold)
                                }

                                if (totalReturnAmount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("ফেরত মাল বাদ (Returns Deducted):", fontSize = 12.sp, color = Color(0xFFDC2626))
                                        Text("- ${Formatter.formatCurrency(totalReturnAmount)}", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (order.discount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("ডিসকাউন্ট বাদ:", fontSize = 12.sp, color = Color(0xFF475569))
                                        Text("- ${Formatter.formatCurrency(order.discount)}", fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFBFDBFE))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("রিটার্ন বাদে চূড়ান্ত বিল (Net Payable):", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                                    Text(
                                        Formatter.formatCurrency(calculatedNet),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                            }
                        }
                    }

                    // Cash Collection & Due Section
                    item {
                        Column {
                            Text(
                                text = "টাকা আদায় ও বকেয়া হিসাব (Collection & Due):",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { paidAmountStr = calculatedNet.toInt().toString() },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("ফুল পেইড", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { paidAmountStr = "0" },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("ফুল বাকি (Due)", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = paidAmountStr,
                                    onValueChange = { paidAmountStr = it },
                                    label = { Text("আদায়কৃত টাকা (Paid ৳)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = StatusDelivered) },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("input_settlement_paid_amount"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = paymentMethod,
                                    onValueChange = { paymentMethod = it },
                                    label = { Text("পদ্ধতি") },
                                    modifier = Modifier.weight(0.8f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (dueAmount > 0) Color(0xFFFEF3C7) else Color(0xFFE6F7ED)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (dueAmount > 0) "দোকানের বকেয়া বাকি (Due Remaining):" else "সকল পাওনা পরিশোধ (Full Paid):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (dueAmount > 0) Color(0xFF92400E) else Color(0xFF065F46)
                                    )
                                    Text(
                                        text = Formatter.formatCurrency(dueAmount),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = if (dueAmount > 0) Color(0xFFB45309) else Color(0xFF047857)
                                    )
                                }
                            }
                        }
                    }

                    // Notes
                    item {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("ডেলিভারি ও কালেকশন নোট") },
                            placeholder = { Text("যেমন: ক্যাশ টাকা রিসিভ করেছে") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_settlement_notes"),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_settlement")
                    ) {
                        Text("বাতিল")
                    }

                    Button(
                        onClick = {
                            val updatedItems = returnItemsStates.map { st ->
                                val retQty = st.returnQty
                                val retAmt = st.returnAmount
                                st.originalItem.copy(
                                    returnedQuantity = retQty,
                                    returnAmount = retAmt,
                                    returnReason = if (retQty > 0) st.returnReason else ""
                                )
                            }

                            val finalStatus = if (selectedStatus == DeliveryStatus.DELIVERED && totalReturnAmount > 0) {
                                if (updatedItems.all { it.returnedQuantity == it.quantity }) DeliveryStatus.RETURNED
                                else DeliveryStatus.PARTIALLY_RETURNED
                            } else {
                                selectedStatus
                            }

                            onConfirmSettlement(
                                finalStatus,
                                deliveryTime.ifBlank { currentTime },
                                paidAmount,
                                paymentMethod,
                                notes,
                                updatedItems
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_save_settlement")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("হিসাব সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOptionChip(
    label: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) color else Color.White,
        shape = RoundedCornerShape(10.dp),
        border = borderStroke(1.dp, if (selected) color else Color(0xFFCBD5E1)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else Color(0xFF475569),
                maxLines = 1
            )
        }
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)
