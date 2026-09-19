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
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.example.data.model.Rider
import com.example.ui.DateFilterType
import com.example.ui.RiderSummary
import com.example.ui.components.Formatter
import com.example.ui.components.ModernDateFilterBar
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.TealPrimary

@Composable
fun RidersScreen(
    riders: List<Rider>,
    riderSummaries: List<RiderSummary>,
    selectedDateFilter: DateFilterType = DateFilterType.TODAY,
    onSelectDateFilter: (DateFilterType) -> Unit = {},
    customStartDate: Long? = null,
    customEndDate: Long? = null,
    onSetCustomDateRange: (Long?, Long?) -> Unit = { _, _ -> },
    onOpenAddRider: () -> Unit,
    onEditRider: (Rider) -> Unit,
    onEditRiderAccount: (Rider) -> Unit,
    onDeleteRider: (Rider) -> Unit,
    onFilterRiderInDeliveries: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("riders_screen")
    ) {
        // Date-to-Date Filter for Riders performance metrics
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

        // Top Header Action - Compact Modern
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
                        text = "ডেলিভারি রাইডার ও একাউন্ট",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "মোট রাইডার: ${riders.size} জন",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = onOpenAddRider,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("btn_add_rider_screen")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন রাইডার", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (riders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোনো ডেলিভারি ম্যান নেই। নতুন রাইডার যোগ করুন।",
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
                items(riders, key = { it.id }) { rider ->
                    val summary = riderSummaries.find { it.rider.id == rider.id }
                    RiderCard(
                        rider = rider,
                        summary = summary,
                        onEdit = { onEditRider(rider) },
                        onDelete = { onDeleteRider(rider) },
                        onEditCredentials = { onEditRiderAccount(rider) },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${rider.phone}"))
                            context.startActivity(intent)
                        },
                        onViewRuns = { onFilterRiderInDeliveries(rider.id) }
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
fun RiderCard(
    rider: Rider,
    summary: RiderSummary?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEditCredentials: () -> Unit,
    onCall: () -> Unit,
    onViewRuns: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rider_card_${rider.id}")
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
                            .background(Color(0xFFE8F1F5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsBike, contentDescription = null, tint = NavySecondary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = rider.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF0F172A)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${rider.zone} • ${rider.phone}",
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
                    // Modern Compact Call Button
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
                            Icon(Icons.Default.Phone, contentDescription = "Call Rider", tint = Color(0xFF059669), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("কল", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        }
                    }

                    // Modern Compact Edit Button
                    Surface(
                        onClick = onEdit,
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, TealPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .height(24.dp)
                            .testTag("btn_edit_rider_${rider.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Rider", tint = TealPrimary, modifier = Modifier.size(11.dp))
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
                            .testTag("btn_delete_rider_${rider.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Rider", tint = Color(0xFFE11D48), modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Credentials Badge Section for Admin
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "আইডি: ${rider.loginEmail.ifBlank { "আইডি নেই" }} | পাস: ${rider.loginPassword.ifBlank { "123" }}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155)
                        )
                    }
                    OutlinedButton(
                        onClick = onEditCredentials,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("পাসওয়ার্ড", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                        Text("যানবাহন", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text(rider.vehicleType, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFF1E293B))
                    }
                    Column {
                        Text("মোট ডেলিভারি", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text("${summary?.deliveredCount ?: 0} টি", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = StatusDelivered)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ক্যাশ কালেকশন", fontSize = 10.sp, color = Color(0xFF64748B))
                        Text(Formatter.formatCurrency(summary?.cashCollected ?: 0.0), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF047857))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onViewRuns,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("চালানসমূহ দেখুন", fontSize = 11.sp)
            }
        }
    }
}
