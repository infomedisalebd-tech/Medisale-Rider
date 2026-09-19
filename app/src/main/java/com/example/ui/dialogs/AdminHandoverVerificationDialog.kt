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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.RiderCustodySummary
import com.example.ui.components.Formatter
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.TealPrimary

@Composable
fun AdminHandoverVerificationDialog(
    custody: RiderCustodySummary,
    onConfirmReceived: (
        cashReceived: Double,
        returnOrderIds: Set<Long>,
        cancelOrderIds: Set<Long>,
        adminNote: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    // Initial cash received defaults to requested cash or total unsettled cash in hand
    val defaultCash = if (custody.isHandoverPending && custody.handoverRequestedCash > 0) {
        custody.handoverRequestedCash
    } else {
        custody.unsettledCashInHand
    }

    var cashReceivedInput by remember {
        mutableStateOf(String.format(java.util.Locale.US, "%.0f", defaultCash))
    }

    // Return orders selection (default all checked)
    var selectedReturnOrderIds by remember {
        mutableStateOf(custody.pendingReturnItems.map { it.orderId }.toSet())
    }

    // Cancelled orders selection (default all checked)
    var selectedCancelOrderIds by remember {
        mutableStateOf(custody.pendingCancelledOrders.map { it.order.id }.toSet())
    }

    var adminNoteInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val enteredReceivedCash = cashReceivedInput.toDoubleOrNull() ?: 0.0
    val remainingCashDue = (custody.unsettledCashInHand - enteredReceivedCash).coerceAtLeast(0.0)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 750.dp)
                .testTag("admin_handover_verification_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Surface(
                    color = NavySecondary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(TealPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "রাইডার ক্যাশ ও প্রোডাক্ট ভেরিফিকেশন",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${custody.rider.name} • ${custody.rider.phone}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Scrollable Content
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Rider Request Status Banner
                    item {
                        if (custody.isHandoverPending) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF93C5FD)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "🔔 রাইডার হ্যান্ডওভার জমা রিকোয়েস্ট পাঠিয়েছেন",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E40AF)
                                        )
                                        Text(
                                            text = "দাবিকৃত ক্যাশ: ${Formatter.formatCurrency(custody.handoverRequestedCash)} | যাচাই করে গ্রহণ করুন।",
                                            fontSize = 11.sp,
                                            color = Color(0xFF1D4ED8)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Cash Audit & Live Calculation Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "১. ক্যাশ কালেকশন ভেরিফিকেশন ও গ্রহণ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))

                                // Metrics grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("রাইডারের মোট কালেকশন", fontSize = 9.5.sp, color = Color(0xFF64748B))
                                            Text(
                                                Formatter.formatCurrency(custody.totalCollectedCash),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("পূর্বে গৃহীত", fontSize = 9.5.sp, color = Color(0xFF047857))
                                            Text(
                                                Formatter.formatCurrency(custody.adminReceivedCash),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF065F46)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFFEF2F2),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("হাতে জমা থাকা ক্যাশ", fontSize = 9.5.sp, color = Color(0xFFB91C1C))
                                            Text(
                                                Formatter.formatCurrency(custody.unsettledCashInHand),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = cashReceivedInput,
                                    onValueChange = {
                                        cashReceivedInput = it
                                        errorMessage = null
                                    },
                                    label = { Text("এডমিন হাতে পাওয়া ক্যাশ গ্রহণ (৳)") },
                                    leadingIcon = {
                                        Text("৳", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary)
                                    },
                                    supportingText = {
                                        Text("যদি পুরো টাকা না পান, যতটুকু ক্যাশ পেয়েছেন তা লিখুন (আংশিক গ্রহণ সম্ভব)")
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_admin_received_cash")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Live Calculation Banner
                                Surface(
                                    color = if (remainingCashDue <= 0.01) Color(0xFFECFDF5) else Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (remainingCashDue <= 0.01) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (remainingCashDue <= 0.01) "✅ সম্পূর্ণ ক্যাশ পরিশোধিত (Fully Cleared)" else "⚠️ রাইডারের কাছে বকেয়া রয়ে গেল (Cash Remaining)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (remainingCashDue <= 0.01) Color(0xFF065F46) else Color(0xFF92400E)
                                            )
                                            if (remainingCashDue > 0.01) {
                                                Text(
                                                    text = "এই বাকি টাকা রাইডারের প্রোফাইলে অ্যালার্ট হিসেবে শো করবে।",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }
                                        Text(
                                            text = Formatter.formatCurrency(remainingCashDue),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = if (remainingCashDue <= 0.01) Color(0xFF059669) else Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Return Medicines Verification Checklist
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.AssignmentReturn,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "২. ফেরত ওষুধ রিসিভ চেক (${custody.pendingReturnItems.size}টি আইটেম)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }

                                    if (custody.pendingReturnItems.isNotEmpty()) {
                                        val allSelected = selectedReturnOrderIds.size == custody.pendingReturnItems.map { it.orderId }.toSet().size
                                        Text(
                                            text = if (allSelected) "সব রিসিভড" else "সব সিলেক্ট করুন",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TealPrimary,
                                            modifier = Modifier.clickable {
                                                selectedReturnOrderIds = if (allSelected) emptySet() else custody.pendingReturnItems.map { it.orderId }.toSet()
                                            }
                                        )
                                    }
                                }

                                if (custody.pendingReturnItems.isEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✅ কোনো পেন্ডিং ফেরত ওষুধ নেই।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    custody.pendingReturnItems.forEachIndexed { index, ret ->
                                        val isChecked = selectedReturnOrderIds.contains(ret.orderId)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedReturnOrderIds = if (isChecked) {
                                                        selectedReturnOrderIds - ret.orderId
                                                    } else {
                                                        selectedReturnOrderIds + ret.orderId
                                                    }
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedReturnOrderIds = if (checked) {
                                                        selectedReturnOrderIds + ret.orderId
                                                    } else {
                                                        selectedReturnOrderIds - ret.orderId
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = TealPrimary),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = ret.productName,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = "${ret.shopName} • চালান #${ret.invoiceNumber} • ${ret.returnReason}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                text = "${ret.returnedQuantity} প্যাক (৳${ret.returnAmount.toInt()})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isChecked) Color(0xFF059669) else Color(0xFFDC2626)
                                            )
                                        }
                                        if (index < custody.pendingReturnItems.size - 1) {
                                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cancelled Parcels Verification Checklist
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "৩. বাতিল পার্সেল গ্রহণ চেক (${custody.pendingCancelledOrders.size}টি চালান)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                    }

                                    if (custody.pendingCancelledOrders.isNotEmpty()) {
                                        val allSelected = selectedCancelOrderIds.size == custody.pendingCancelledOrders.size
                                        Text(
                                            text = if (allSelected) "সব রিসিভড" else "সব সিলেক্ট করুন",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TealPrimary,
                                            modifier = Modifier.clickable {
                                                selectedCancelOrderIds = if (allSelected) emptySet() else custody.pendingCancelledOrders.map { it.order.id }.toSet()
                                            }
                                        )
                                    }
                                }

                                if (custody.pendingCancelledOrders.isEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✅ কোনো পেন্ডিং বাতিল পার্সেল নেই।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    custody.pendingCancelledOrders.forEachIndexed { index, ord ->
                                        val isChecked = selectedCancelOrderIds.contains(ord.order.id)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCancelOrderIds = if (isChecked) {
                                                        selectedCancelOrderIds - ord.order.id
                                                    } else {
                                                        selectedCancelOrderIds + ord.order.id
                                                    }
                                                }
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedCancelOrderIds = if (checked) {
                                                        selectedCancelOrderIds + ord.order.id
                                                    } else {
                                                        selectedCancelOrderIds - ord.order.id
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = TealPrimary),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "#${ord.order.invoiceNumber} - ${ord.order.shopName}",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF0F172A)
                                                )
                                                Text(
                                                    text = "কারণ: ${ord.order.cancelReason ?: ord.order.notes.ifBlank { "অর্ডার বাতিল" }}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                text = Formatter.formatCurrency(ord.order.grossAmount),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (isChecked) Color(0xFF059669) else Color(0xFFDC2626)
                                            )
                                        }
                                        if (index < custody.pendingCancelledOrders.size - 1) {
                                            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Admin Settlement Note
                    item {
                        OutlinedTextField(
                            value = adminNoteInput,
                            onValueChange = { adminNoteInput = it },
                            label = { Text("এডমিন ভেরিফিকেশন ও সেটেলমেন্ট নোট") },
                            placeholder = { Text("যেমন: ক্যাশ ৳৪,৫০০ ও ২টি রিটার্ন ওষুধ রিসিভড...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admin_settlement_notes"),
                            minLines = 2,
                            maxLines = 3
                        )
                    }
                }

                // Actions Footer
                Surface(
                    color = Color(0xFFF8FAFC),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("বাতিল")
                        }

                        Button(
                            onClick = {
                                val amount = cashReceivedInput.toDoubleOrNull() ?: 0.0
                                if (amount < 0) {
                                    errorMessage = "সঠিক টাকার পরিমাণ দিন"
                                    return@Button
                                }
                                onConfirmReceived(
                                    amount,
                                    selectedReturnOrderIds,
                                    selectedCancelOrderIds,
                                    adminNoteInput
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .weight(1.6f)
                                .testTag("btn_confirm_admin_handover")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("রিসিভ কনফার্ম করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
