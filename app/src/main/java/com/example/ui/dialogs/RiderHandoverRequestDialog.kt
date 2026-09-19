package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun RiderHandoverRequestDialog(
    custody: RiderCustodySummary,
    onSubmit: (requestedCash: Double, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var cashInput by remember {
        mutableStateOf(if (custody.unsettledCashInHand > 0) String.format(java.util.Locale.US, "%.0f", custody.unsettledCashInHand) else "0")
    }
    var notesInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("rider_handover_request_dialog"),
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
                                    .size(36.dp)
                                    .background(TealPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "এডমিনকে ক্যাশ ও মাল জমা দিন",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Handover & Custody Settlement Request",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
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
                    // Current Status Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📊 আপনার বর্তমান মোট হেফাজত হিসাব (Current Custody)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFECFDF5),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(text = "সংগৃহীত ক্যাশ", fontSize = 10.sp, color = Color(0xFF047857))
                                            Text(
                                                text = Formatter.formatCurrency(custody.totalCollectedCash),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF065F46)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(text = "এডমিন রিসিভড", fontSize = 10.sp, color = Color(0xFF1D4ED8))
                                            Text(
                                                text = Formatter.formatCurrency(custody.adminReceivedCash),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = Color(0xFF1E40AF)
                                            )
                                        }
                                    }

                                    Surface(
                                        color = if (custody.unsettledCashInHand > 0) Color(0xFFFEF2F2) else Color(0xFFF8FAFC),
                                        shape = RoundedCornerShape(8.dp),
                                        border = if (custody.unsettledCashInHand > 0) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)) else null,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(text = "হাতে থাকা ক্যাশ", fontSize = 10.sp, color = Color(0xFFB91C1C))
                                            Text(
                                                text = Formatter.formatCurrency(custody.unsettledCashInHand),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cash Submission Field
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                                        text = "১. জমা দেওয়ার ক্যাশ পরিমাণ (Tk)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "এডমিনকে যে পরিমাণ টাকা সরাসরি বা ডিজিটাল মাধ্যমে জমা দিচ্ছেন:",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = cashInput,
                                    onValueChange = {
                                        cashInput = it
                                        errorMessage = null
                                    },
                                    label = { Text("জমা টাকার পরিমাণ (৳)") },
                                    leadingIcon = {
                                        Text("৳", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TealPrimary)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_handover_cash")
                                )

                                if (errorMessage != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = errorMessage!!,
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // Return Medicines Summary
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
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
                                            text = "২. ফেরত আসা ওষুধ (${custody.pendingReturnItems.size}টি আইটেম)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                    Text(
                                        text = "মোট: " + Formatter.formatCurrency(custody.totalReturnAmount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFB45309)
                                    )
                                }

                                if (custody.pendingReturnItems.isEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✅ আপনার কাছে বর্তমানে কোনো ফেরত ওষুধ জমা নেই।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    custody.pendingReturnItems.forEachIndexed { index, ret ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${index + 1}. ${ret.productName}",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF78350F)
                                                )
                                                Text(
                                                    text = "${ret.shopName} • চালান #${ret.invoiceNumber} • ${ret.returnReason}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF92400E)
                                                )
                                            }
                                            Text(
                                                text = "${ret.returnedQuantity} প্যাক (৳${ret.returnAmount.toInt()})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFFB45309)
                                            )
                                        }
                                        if (index < custody.pendingReturnItems.size - 1) {
                                            HorizontalDivider(color = Color(0xFFFDE68A).copy(alpha = 0.5f), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cancelled Parcels Summary
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
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
                                            text = "৩. বাতিল হওয়া পার্সেল (${custody.pendingCancelledOrders.size}টি চালান)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                    }
                                }

                                if (custody.pendingCancelledOrders.isEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✅ আপনার কাছে কোনো বাতিল হওয়া পার্সেল জমা নেই।",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    custody.pendingCancelledOrders.forEachIndexed { index, ord ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${index + 1}. #${ord.order.invoiceNumber} - ${ord.order.shopName}",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF991B1B)
                                                )
                                                Text(
                                                    text = "কারণ: ${ord.order.cancelReason ?: ord.order.notes.ifBlank { "অর্ডার বাতিল" }}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFB91C1C)
                                                )
                                            }
                                            Text(
                                                text = Formatter.formatCurrency(ord.order.grossAmount),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                        if (index < custody.pendingCancelledOrders.size - 1) {
                                            HorizontalDivider(color = Color(0xFFFECACA).copy(alpha = 0.5f), thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Rider Notes Field
                    item {
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("এডমিনের জন্য নোট বা বিবরণ (ঐচ্ছিক)") },
                            placeholder = { Text("যেমন: সকাল ও দুপুরের ক্যাশ জমা দিচ্ছি...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_handover_notes"),
                            minLines = 2,
                            maxLines = 3
                        )
                    }

                    // Notice / Alert Info
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "রিকোয়েস্ট পাঠানোর পর এডমিন ভেরিফাই করে কনফার্ম করলে আপনার একাউন্ট থেকে ব্যালেন্স ও প্রোডাক্ট স্বয়ংক্রিয়ভাবে কমে যাবে।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Action Buttons Footer
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
                                val amount = cashInput.toDoubleOrNull() ?: 0.0
                                if (amount < 0) {
                                    errorMessage = "সঠিক টাকার পরিমাণ দিন"
                                    return@Button
                                }
                                onSubmit(amount, notesInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .weight(1.6f)
                                .testTag("btn_submit_handover_request")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("এডমিনকে জমা পাঠান", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
