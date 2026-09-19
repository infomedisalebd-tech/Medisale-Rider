package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryStatus
import com.example.data.model.OrderWithItems
import com.example.data.model.Rider
import com.example.data.model.User
import com.example.ui.RiderCustodySummary
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.Formatter
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.TealPrimary

@Composable
fun RiderPortalScreen(
    user: User,
    rider: Rider?,
    orders: List<OrderWithItems>,
    custody: RiderCustodySummary? = null,
    onRequestHandover: () -> Unit = {},
    onViewHandoverHistory: () -> Unit = {},
    onOpenSettlement: (OrderWithItems) -> Unit,
    onOpenInvoiceSlip: (OrderWithItems) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalAssigned = orders.size
    val deliveredCount = orders.count { it.order.status == DeliveryStatus.DELIVERED.name || it.order.status == DeliveryStatus.PARTIALLY_RETURNED.name }
    val pendingCount = orders.count { it.order.status == DeliveryStatus.DISPATCHED.name }
    val returnCount = orders.count { it.order.status == DeliveryStatus.RETURNED.name || it.order.status == DeliveryStatus.PARTIALLY_RETURNED.name }
    val totalCollected = orders.sumOf { it.order.paidAmount }
    val totalDue = orders.sumOf { it.order.dueAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("rider_portal_screen")
    ) {
        // Rider Header
        Surface(
            color = NavySecondary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(TealPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = rider?.name ?: user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = TealPrimary.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "MEDISALE BD • রাইডার",
                                        color = Color(0xFF5EEAD4),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "জোন: ${rider?.zone ?: "সব এলাকা"} • গাড়ি: ${rider?.vehicleType ?: "Motorcycle"}",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onViewHandoverHistory,
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), CircleShape)
                                .testTag("btn_rider_handover_history")
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "হ্যান্ডওভার হিস্টোরি",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), CircleShape)
                                .testTag("btn_rider_logout")
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mini stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RiderMetricCard(
                        title = "ক্যাশ কালেকশন",
                        value = Formatter.formatCurrency(totalCollected),
                        accentColor = Color(0xFF4ADE80),
                        modifier = Modifier.weight(1f)
                    )
                    RiderMetricCard(
                        title = "হাতে থাকা ক্যাশ",
                        value = Formatter.formatCurrency(custody?.unsettledCashInHand ?: 0.0),
                        accentColor = if ((custody?.unsettledCashInHand ?: 0.0) > 0) Color(0xFFF87171) else Color(0xFF4ADE80),
                        modifier = Modifier.weight(1f)
                    )
                    RiderMetricCard(
                        title = "ডেলিভারি",
                        value = "$deliveredCount / $totalAssigned",
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Custody & Handover Alert Banner Card (If rider has pending cash/returns/cancels)
        if (custody != null && custody.hasPendingCustody) {
            Surface(
                color = if (custody.isHandoverPending) Color(0xFFEFF6FF) else Color(0xFFFFFBEB),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (custody.isHandoverPending) Color(0xFF93C5FD) else Color(0xFFFDE68A)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("rider_custody_alert_banner")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (custody.isHandoverPending) Icons.Default.HourglassTop else Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = if (custody.isHandoverPending) Color(0xFF2563EB) else Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (custody.isHandoverPending) "⏳ জমা রিকোয়েস্ট পেন্ডিং (Pending Admin Check)" else "⚠️ এডমিনের কাছে ক্যাশ ও প্রোডাক্ট জমা দেওয়া বাকি",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (custody.isHandoverPending) Color(0xFF1E40AF) else Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (custody.unsettledCashInHand > 0) {
                            Text(
                                text = "💵 ক্যাশ: ৳${custody.unsettledCashInHand.toInt()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB91C1C)
                            )
                        }
                        if (custody.totalReturnCount > 0) {
                            Text(
                                text = "📦 ফেরত ওষুধ: ${custody.totalReturnCount}টি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        }
                        if (custody.totalCancelledCount > 0) {
                            Text(
                                text = "🚫 বাতিল পার্সেল: ${custody.totalCancelledCount}টি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onRequestHandover,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (custody.isHandoverPending) Color(0xFF2563EB) else TealPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_request_handover_open")
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (custody.isHandoverPending) "হ্যান্ডওভার আপডেট করুন" else "💸 এডমিনকে জমা দিন",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else if (custody != null && !custody.hasPendingCustody && orders.isNotEmpty()) {
            Surface(
                color = Color(0xFFECFDF5),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "✅ আপনার সকল ক্যাশ ও প্রোডাক্ট এডমিনকে সফলভাবে বুঝিয়ে দেওয়া হয়েছে।",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF065F46)
                    )
                }
            }
        }

        // Section Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "আমার আজকের ডেলিভারি চালান (${orders.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "পেন্ডিং: $pendingCount | রিটার্ন: $returnCount",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "আপনার নামে কোনো ডেলিভারি অর্পিত হয়নি",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "নতুন অর্ডার এডমিন থেকে যুক্ত হলে এখানে দেখা যাবে।",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.order.id }) { item ->
                    RiderOrderCard(
                        item = item,
                        onCallShop = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.order.shopPhone}"))
                            context.startActivity(intent)
                        },
                        onOpenSettlement = { onOpenSettlement(item) },
                        onOpenInvoice = { onOpenInvoiceSlip(item) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun RiderMetricCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0x1AFFFFFF),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)) {
            Text(
                text = title,
                fontSize = 9.5.sp,
                color = Color(0xFFCBD5E1)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}

@Composable
fun RiderOrderCard(
    item: OrderWithItems,
    onCallShop: () -> Unit,
    onOpenSettlement: () -> Unit,
    onOpenInvoice: () -> Unit
) {
    val order = item.order
    val items = item.items

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rider_order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Invoice No & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.invoiceNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NavySecondary
                    )
                    if (order.assignedTime.isNotBlank()) {
                        Text(
                            text = " • ${order.assignedTime}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                DeliveryStatusBadge(statusStr = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shop Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = order.shopName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    if (order.shopAddress.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp, start = 20.dp)
                        ) {
                            Text(
                                text = order.shopAddress,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (order.shopPhone.isNotBlank()) {
                    Surface(
                        onClick = onCallShop,
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Pharmacy", tint = Color(0xFF059669), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("কল", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Items Summary
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ওষুধের তালিকা (${items.size} আইটেম):",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    items.take(3).forEach { prod ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${prod.productName} (x${prod.quantity})",
                                fontSize = 10.5.sp,
                                color = Color(0xFF334155)
                            )
                            Text(
                                text = Formatter.formatCurrency(prod.totalPrice),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                    if (items.size > 3) {
                        Text(
                            text = "+ আরও ${items.size - 3} টি আইটেম...",
                            fontSize = 9.5.sp,
                            color = TealPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Financial breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("চালান মূল্য", fontSize = 10.sp, color = Color(0xFF64748B))
                    Text(Formatter.formatCurrency(order.grossAmount), fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = Color(0xFF1E293B))
                }
                if (order.returnAmount > 0) {
                    Column {
                        Text("রিটার্ন বাদ", fontSize = 10.sp, color = StatusReturned)
                        Text("-${Formatter.formatCurrency(order.returnAmount)}", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = StatusReturned)
                    }
                }
                Column {
                    Text("সংগৃহীত ক্যাশ", fontSize = 10.sp, color = Color(0xFF047857))
                    Text(Formatter.formatCurrency(order.paidAmount), fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF047857))
                }
                if (order.dueAmount > 0) {
                    Column {
                        Text("বকেয়া বাকি", fontSize = 10.sp, color = Color(0xFFDC2626))
                        Text(Formatter.formatCurrency(order.dueAmount), fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFDC2626))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenInvoice,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("চালান স্লিপ", fontSize = 11.5.sp)
                }

                Button(
                    onClick = onOpenSettlement,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(36.dp)
                        .testTag("btn_settle_rider_${order.id}")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("হিসাব ও ডেলিভারি", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
