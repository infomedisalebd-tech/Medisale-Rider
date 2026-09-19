package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryStatus
import com.example.data.model.OrderWithItems
import com.example.ui.DateFilterType
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.Formatter
import com.example.ui.components.ModernDateFilterBar
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.TealPrimary

@Composable
fun DeliveriesScreen(
    orders: List<OrderWithItems>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedStatusFilter: String?,
    onSelectStatusFilter: (String?) -> Unit,
    selectedDateFilter: DateFilterType = DateFilterType.TODAY,
    onSelectDateFilter: (DateFilterType) -> Unit = {},
    customStartDate: Long? = null,
    customEndDate: Long? = null,
    onSetCustomDateRange: (Long?, Long?) -> Unit = { _, _ -> },
    onOpenCreateOrder: () -> Unit,
    onOpenSmartUpload: () -> Unit = {},
    onChangeRider: (OrderWithItems) -> Unit = {},
    onOpenSettlement: (OrderWithItems) -> Unit,
    onOpenInvoiceSlip: (OrderWithItems) -> Unit,
    onEditOrder: (OrderWithItems) -> Unit = {},
    onDeleteOrder: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("deliveries_screen")
    ) {
        // Top Action & Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Dispatch Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onOpenSmartUpload,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("btn_open_smart_upload")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("📸 PDF/ছবি থেকে অটো চালান", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }

                Button(
                    onClick = onOpenCreateOrder,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                        .testTag("btn_open_create_order")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("+ নতুন চালান", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }

            ModernDateFilterBar(
                selectedDateFilter = selectedDateFilter,
                onSelectDateFilter = onSelectDateFilter,
                customStartDate = customStartDate,
                customEndDate = customEndDate,
                onSetCustomRange = onSetCustomDateRange,
                title = "তারিখ"
            )
        }

        // Search & Filter Bar - Sleek & Compact
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("দোকানের নাম, চালান নং, রাইডার বা ঔষধ খুঁজুন...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_deliveries_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Filter chips
                val filterOptions = listOf(
                    null to "সকল (${orders.size})",
                    DeliveryStatus.DISPATCHED.name to "চলমান",
                    DeliveryStatus.DELIVERED.name to "ডেলিভার্ড",
                    DeliveryStatus.PARTIALLY_RETURNED.name to "আংশিক ফেরত",
                    DeliveryStatus.RETURNED.name to "সম্পূর্ণ ফেরত",
                    DeliveryStatus.CANCELLED.name to "বাতিল"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    filterOptions.forEach { (status, label) ->
                        val isSelected = selectedStatusFilter == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectStatusFilter(status) },
                            label = { Text(label, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("chip_filter_${status ?: "all"}")
                        )
                    }
                }
            }
        }

        // Deliveries List
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "কোনো ডেলিভারি চালান পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "নতুন চালান তৈরি করতে নিচের বাটনে চাপ দিন",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onOpenCreateOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("নতুন ডেলিভারি চালান তৈরি করুন", fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(orders, key = { it.order.id }) { orderWithItems ->
                    DeliveryOrderCard(
                        orderWithItems = orderWithItems,
                        onChangeRider = { onChangeRider(orderWithItems) },
                        onOpenSettlement = { onOpenSettlement(orderWithItems) },
                        onOpenInvoiceSlip = { onOpenInvoiceSlip(orderWithItems) },
                        onEdit = { onEditOrder(orderWithItems) },
                        onDelete = { onDeleteOrder(orderWithItems.order.id) },
                        onCallPhone = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun DeliveryOrderCard(
    orderWithItems: OrderWithItems,
    onChangeRider: () -> Unit = {},
    onOpenSettlement: () -> Unit,
    onOpenInvoiceSlip: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCallPhone: (String) -> Unit
) {
    val order = orderWithItems.order
    val items = orderWithItems.items
    val returnedItems = items.filter { it.returnedQuantity > 0 }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Invoice No, Status Badge, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFE0F4F5),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = order.invoiceNumber,
                            color = TealPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    DeliveryStatusBadge(statusStr = order.status)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Modern Compact Challan Edit Button
                    Surface(
                        onClick = onEdit,
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, TealPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("btn_edit_order_${order.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit order", tint = TealPrimary, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("এডিট", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }

                    // Modern Compact Delete Button
                    Surface(
                        onClick = onDelete,
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFFECDD3)),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("btn_delete_order_${order.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete order", tint = Color(0xFFE11D48), modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Shop Name & Contact
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
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                    }
                    Text(
                        text = order.shopAddress,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }

                if (order.shopPhone.isNotBlank()) {
                    Surface(
                        onClick = { onCallPhone(order.shopPhone) },
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Shop", tint = Color(0xFF059669), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("কল", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rider Assigned & Timing with Change Rider button
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1B4965), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "রাইডার: ${order.riderName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B4965),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.clickable { onChangeRider() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("বদল", color = Color(0xFF0284C7), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (order.deliveryTime != null) "ডেলিভারি: ${order.deliveryTime}" else "রওয়ানা: ${order.assignedTime}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (order.deliveryTime != null) Color(0xFF047857) else Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Medicine Products Preview (Compact modern box)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "📦 ঔষধ তালিকা (${items.size} আইটেম):",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                items.take(3).forEach { item ->
                    Text(
                        text = "• ${item.productName} — ${item.quantity} ${item.packType} (৳${item.totalPrice.toInt()})",
                        fontSize = 11.5.sp,
                        color = Color(0xFF334155)
                    )
                }
                if (items.size > 3) {
                    Text(
                        text = "+ আরও ${items.size - 3} টি আইটেম...",
                        fontSize = 10.5.sp,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // If items returned
                if (returnedItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    returnedItems.forEach { ret ->
                        Text(
                            text = "↩️ ফেরত: ${ret.productName} (${ret.returnedQuantity} ${ret.packType} - ৳${ret.returnAmount.toInt()})",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusReturned
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Financial Summary Bar (Gross -> Return -> Net -> Paid vs Due)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "চালান: ${Formatter.formatCurrency(order.grossAmount)}" +
                                (if (order.returnAmount > 0) " (ফেরত: -${Formatter.formatCurrency(order.returnAmount)})" else ""),
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "চূড়ান্ত বিল: ${Formatter.formatCurrency(order.netAmount)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.5.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "আদায়: ${Formatter.formatCurrency(order.paidAmount)}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF047857)
                    )
                    if (order.dueAmount > 0) {
                        Text(
                            text = "বাকি: ${Formatter.formatCurrency(order.dueAmount)}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    } else {
                        Text(
                            text = "পরিশোধ সম্পন্ন",
                            fontSize = 10.5.sp,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

            // Action Buttons: Settlement & Invoice Slip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenInvoiceSlip,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("btn_view_slip_${order.id}")
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("মেমো রসিদ", fontSize = 11.5.sp)
                }

                Button(
                    onClick = onOpenSettlement,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(36.dp)
                        .testTag("btn_open_settlement_${order.id}")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("হিসাব ও রিটার্ন", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                }
            }
        }
    }
}
