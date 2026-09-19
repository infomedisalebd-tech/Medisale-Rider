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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.OrderWithItems
import com.example.data.model.Shop
import com.example.ui.DateFilterType
import com.example.ui.components.Formatter
import com.example.ui.components.ModernDateFilterBar
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.TealPrimary

@Composable
fun ShopsScreen(
    shops: List<Shop>,
    orders: List<OrderWithItems>,
    selectedDateFilter: DateFilterType = DateFilterType.TODAY,
    onSelectDateFilter: (DateFilterType) -> Unit = {},
    customStartDate: Long? = null,
    customEndDate: Long? = null,
    onSetCustomDateRange: (Long?, Long?) -> Unit = { _, _ -> },
    onOpenAddShop: () -> Unit,
    onEditShop: (Shop) -> Unit,
    onDeleteShop: (Shop) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("shops_screen")
    ) {
        // Date-to-Date Filter for Shops
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            ModernDateFilterBar(
                selectedDateFilter = selectedDateFilter,
                onSelectDateFilter = onSelectDateFilter,
                customStartDate = customStartDate,
                customEndDate = customEndDate,
                onSetCustomRange = onSetCustomDateRange,
                title = "তারিখ"
            )
        }

        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ফার্মেসি ও দোকান তালিকা",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "মোট নিবন্ধিত ফার্মেসি: ${shops.size} টি",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = onOpenAddShop,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_add_shop_screen")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন দোকান", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (shops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোনো দোকান নেই। নতুন ফার্মেসি যুক্ত করুন।",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shops, key = { it.id }) { shop ->
                    val shopOrders = orders.filter { it.order.shopId == shop.id || it.order.shopName.equals(shop.name, ignoreCase = true) }
                    val totalDue = shopOrders.sumOf { it.order.dueAmount }

                    ShopCard(
                        shop = shop,
                        totalOrders = shopOrders.size,
                        totalDue = totalDue,
                        onEdit = { onEditShop(shop) },
                        onDelete = { onDeleteShop(shop) },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}"))
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
fun ShopCard(
    shop: Shop,
    totalOrders: Int,
    totalDue: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shop_card_${shop.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFE0F4F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = shop.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${shop.area} • ${shop.phone.ifBlank { shop.address }}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (shop.phone.isNotBlank()) {
                        Surface(
                            onClick = onCall,
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

                    Surface(
                        onClick = onEdit,
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, TealPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("btn_edit_shop_${shop.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Shop", tint = TealPrimary, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("এডিট", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TealPrimary)
                        }
                    }

                    Surface(
                        onClick = onDelete,
                        color = Color(0xFFFFF1F2),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFFECDD3)),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("btn_delete_shop_${shop.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Shop", tint = Color(0xFFE11D48), modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ম্যানেজার/মালিক", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text(shop.ownerOrManager.ifBlank { "N/A" }, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF1E293B))
                    }
                    Column {
                        Text("মোট চালান", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text("$totalOrders টি", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TealPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("বকেয়া পাওনা", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text(
                            Formatter.formatCurrency(totalDue),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (totalDue > 0) Color(0xFFDC2626) else Color(0xFF047857)
                        )
                    }
                }
            }
        }
    }
}
