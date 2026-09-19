package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Rider
import com.example.ui.DashboardMetrics
import com.example.ui.DateFilterType
import com.example.ui.RiderCustodySummary
import com.example.ui.RiderSummary
import com.example.ui.components.Formatter
import com.example.ui.components.MetricCard
import com.example.ui.components.ModernDateFilterBar
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.TealPrimary
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    metrics: DashboardMetrics,
    selectedDateFilter: DateFilterType,
    onSelectDateFilter: (DateFilterType) -> Unit,
    customStartDate: Long? = null,
    customEndDate: Long? = null,
    onSetCustomDateRange: (Long?, Long?) -> Unit = { _, _ -> },
    riders: List<Rider>,
    selectedRiderId: Long?,
    onSelectRider: (Long?) -> Unit,
    riderSummaries: List<RiderSummary>,
    riderCustodySummaries: List<RiderCustodySummary> = emptyList(),
    onOpenCreateOrder: () -> Unit,
    onOpenSmartUpload: () -> Unit = {},
    onOpenAddRider: () -> Unit,
    onOpenAddShop: () -> Unit,
    onOpenHandoverVerification: (RiderCustodySummary) -> Unit = {},
    onOpenHandoverHistory: () -> Unit = {},
    onNavigateToDeliveries: (statusFilter: String?) -> Unit,
    onExportReport: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var riderDropdownExpanded by remember { mutableStateOf(false) }
    val selectedRiderName = riders.find { it.id == selectedRiderId }?.name ?: "সকল ডেলিভারি ম্যান (All Riders)"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Hero Header with Live Pulse - Compact & Modern
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF00565B), Color(0xFF00838F), Color(0xFF1B4965))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF4ADE80), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "লাইভ ঔষধ ডেলিভারি ড্যাশবোর্ড",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.4.sp
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Auto-Calculated",
                                color = Color.White,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "MEDISALE BD",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "ডেলিভারি ট্র্যাকিং, প্রোডাক্ট রিটার্ন ও স্বয়ংক্রিয় হিসাব",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Action Buttons in Header - Sleek and compact
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onOpenSmartUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(38.dp)
                                .testTag("header_btn_smart_upload")
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("📸 অটো চালান", color = TealPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 11.5.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenCreateOrder,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(38.dp)
                                .testTag("header_btn_new_dispatch")
                        ) {
                            Text("+ ম্যানুয়াল", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onOpenAddRider,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .height(38.dp)
                                .testTag("header_btn_add_rider")
                        ) {
                            Text("+ রাইডার", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = onOpenAddShop,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(0.8f)
                                .height(38.dp)
                                .testTag("header_btn_add_shop")
                        ) {
                            Text("+ দোকান", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Filters Section (Modern Date-to-Date Filter Bar & Delivery Man Dropdown) - Sleek & Compact
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModernDateFilterBar(
                    selectedDateFilter = selectedDateFilter,
                    onSelectDateFilter = onSelectDateFilter,
                    customStartDate = customStartDate,
                    customEndDate = customEndDate,
                    onSetCustomRange = onSetCustomDateRange,
                    title = "তারিখ"
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        // Rider Selector Dropdown
                        ExposedDropdownMenuBox(
                            expanded = riderDropdownExpanded,
                            onExpandedChange = { riderDropdownExpanded = !riderDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedRiderName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("ডেলিভারি ম্যান ফিল্টার (Rider Filter)", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = riderDropdownExpanded) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("filter_rider_dropdown"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = riderDropdownExpanded,
                                onDismissRequest = { riderDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("সকল ডেলিভারি ম্যান (All Riders)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                    onClick = {
                                        onSelectRider(null)
                                        riderDropdownExpanded = false
                                    }
                                )
                                riders.forEach { rider ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(rider.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                Text("${rider.zone} • ${rider.phone}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            onSelectRider(rider.id)
                                            riderDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Export Report Section (PDF & Excel / CSV)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📊 রিপোর্ট ডাউনলোড ও এক্সপোর্ট",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "দৈনিক বা তারিখ অনুযায়ী হিসাব PDF বা Excel/CSV ফাইলে শেয়ার করুন",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { onExportReport("PDF") },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_export_pdf")
                        ) {
                            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onExportReport("EXCEL") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_export_excel")
                        ) {
                            Text("Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Title: Deliveries Statistics
        item {
            Text(
                text = "📦 ডেলিভারি সংখ্যা ও স্ট্যাটাস রিপোর্ট",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        // 2x2 Grid of Delivery Counts - Compact 8.dp gap
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "মোট চালান",
                        value = "${metrics.totalOrdersCount} টি",
                        subtitle = "মূল্য: ${Formatter.formatCurrency(metrics.totalGrossAmount)}",
                        icon = Icons.Default.ReceiptLong,
                        iconBgColor = Color(0xFFE0F4F5),
                        iconTint = TealPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDeliveries(null) }
                    )

                    MetricCard(
                        title = "ডেলিভারি সম্পন্ন",
                        value = "${metrics.deliveredCount} টি",
                        subtitle = "মূল্য: ${Formatter.formatCurrency(metrics.deliveredAmount)}",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = Color(0xFFE6F7ED),
                        iconTint = StatusDelivered,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDeliveries("DELIVERED") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "চলমান / বাকি",
                        value = "${metrics.pendingCount} টি",
                        subtitle = "বাকি: ${Formatter.formatCurrency(metrics.pendingAmount)}",
                        icon = Icons.Default.LocalShipping,
                        iconBgColor = Color(0xFFFEF3C7),
                        iconTint = StatusPending,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDeliveries("DISPATCHED") }
                    )

                    MetricCard(
                        title = "ফেরত / রিটার্ন",
                        value = "${metrics.returnCount} টি",
                        subtitle = "ফেরত: ${Formatter.formatCurrency(metrics.totalReturnAmount)}",
                        icon = Icons.Default.KeyboardReturn,
                        iconBgColor = Color(0xFFFEE2E2),
                        iconTint = StatusReturned,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToDeliveries("RETURNED") }
                    )
                }

                if (metrics.cancelledCount > 0) {
                    MetricCard(
                        title = "বাতিল ডেলিভারি",
                        value = "${metrics.cancelledCount} টি",
                        subtitle = "দোকানদার নেয়নি / বাতিল",
                        icon = Icons.Default.Cancel,
                        iconBgColor = Color(0xFFF3F4F6),
                        iconTint = StatusCancelled,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToDeliveries("CANCELLED") }
                    )
                }
            }
        }

        // Section Title: Financial Auto-Calculation
        item {
            Text(
                text = "💰 টাকা আদায় ও বকেয়া হিসাব (Financials)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        // Comprehensive Financial Summary Card - Compact Modern
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Net Bill (Return Amount bad diye bill koto)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "রিটার্ন বাদে সর্বমোট নিট বিল",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "(Gross Bill - Return Amount)",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        Text(
                            text = Formatter.formatCurrency(metrics.totalNetAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TealPrimary
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                    // 2 Compact Tiles: Collected Cash vs Due Remaining
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total Collected Cash
                        Surface(
                            color = Color(0xFFE6F7ED),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = StatusDelivered, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("নগদ আদায়", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = Formatter.formatCurrency(metrics.totalCollectedAmount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF047857)
                                )
                                Text("আদায় হার: ${metrics.collectionRate.toInt()}%", fontSize = 10.sp, color = Color(0xFF059669))
                            }
                        }

                        // Total Due Balance
                        Surface(
                            color = if (metrics.totalDueAmount > 0) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WarningAmber, contentDescription = null, tint = if (metrics.totalDueAmount > 0) Color(0xFFD97706) else Color(0xFF64748B), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("দোকানের বাকি", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (metrics.totalDueAmount > 0) Color(0xFF92400E) else Color(0xFF475569))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = Formatter.formatCurrency(metrics.totalDueAmount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (metrics.totalDueAmount > 0) Color(0xFFB45309) else Color(0xFF64748B)
                                )
                                Text(
                                    text = if (metrics.totalDueAmount > 0) "বকেয়া বাকি" else "কোনো বাকি নেই",
                                    fontSize = 10.sp,
                                    color = if (metrics.totalDueAmount > 0) Color(0xFFD97706) else Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar of Delivery Completion
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ডেলিভারি সম্পন্নতার হার", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("${metrics.deliveryCompletionRate.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (metrics.deliveryCompletionRate / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TealPrimary,
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }

        // Section Title: Delivery Riders Live Status & Handover Audit
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🛵 রাইডার হ্যান্ডওভার ও ক্যাশ হেফাজত",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onOpenHandoverHistory,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("লগ হিস্টোরি", fontSize = 10.5.sp)
                    }
                }
            }
        }

        // Rider Handover & Performance Cards - Modern & Interactive
        items(riderCustodySummaries) { custody ->
            val rider = custody.rider
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (custody.isHandoverPending) Color(0xFFF0FDF4) else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (custody.isHandoverPending) Color(0xFF86EFAC)
                    else if (custody.hasPendingCustody) Color(0xFFFED7AA)
                    else Color(0xFFF1F5F9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rider_custody_card_${rider.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (custody.isHandoverPending) Color(0xFFDCFCE7) else Color(0xFFE8F1F5),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (custody.isHandoverPending) Color(0xFF16A34A) else NavySecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = rider.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${rider.zone} • ${rider.phone}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Handover status pill
                        Surface(
                            color = when {
                                custody.isHandoverPending -> Color(0xFF2563EB)
                                custody.hasPendingCustody -> Color(0xFFFEF3C7)
                                else -> Color(0xFFECFDF5)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = when {
                                    custody.isHandoverPending -> "⏳ রিকোয়েস্ট জমা এসেছে"
                                    custody.hasPendingCustody -> "⚠️ হেফাজত বাকি"
                                    else -> "✅ ক্লিয়ার"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    custody.isHandoverPending -> Color.White
                                    custody.hasPendingCustody -> Color(0xFFB45309)
                                    else -> Color(0xFF047857)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                    // Real-time custody statistics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("মোট কালেকশন", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text(Formatter.formatCurrency(custody.totalCollectedCash), fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp, color = Color(0xFF334155))
                        }
                        Column {
                            Text("হাতে থাকা ক্যাশ", fontSize = 10.sp, color = if (custody.unsettledCashInHand > 0) Color(0xFFB91C1C) else Color(0xFF047857))
                            Text(
                                Formatter.formatCurrency(custody.unsettledCashInHand),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (custody.unsettledCashInHand > 0) Color(0xFFDC2626) else Color(0xFF047857)
                            )
                        }
                        Column {
                            Text("ফেরত ওষুধ", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("${custody.totalReturnCount} টি", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = if (custody.totalReturnCount > 0) Color(0xFFD97706) else Color(0xFF64748B))
                        }
                        Column {
                            Text("বাতিল পার্সেল", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("${custody.totalCancelledCount} টি", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = if (custody.totalCancelledCount > 0) Color(0xFFDC2626) else Color(0xFF64748B))
                        }
                    }

                    // Admin Action Button for Handover Verification
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onOpenHandoverVerification(custody) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (custody.isHandoverPending) Color(0xFF2563EB) else TealPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_verify_handover_${rider.id}")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (custody.isHandoverPending) "রিকোয়েস্ট ভেরিফাই ও রিসিভ" else "হিসাব ও মাল রিসিভ করুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
