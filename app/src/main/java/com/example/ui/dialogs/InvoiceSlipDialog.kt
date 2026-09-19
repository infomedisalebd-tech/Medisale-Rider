package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.OrderWithItems
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.Formatter
import com.example.ui.theme.TealPrimary

@Composable
fun InvoiceSlipDialog(
    orderWithItems: OrderWithItems,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val order = orderWithItems.order
    val items = orderWithItems.items

    val returnItems = items.filter { it.returnedQuantity > 0 }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
                .testTag("invoice_slip_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মেমো ও চালান রসিদ (Delivery Memo)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_invoice_slip_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE2E8F0))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Company Header
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💊 MEDISALE BD",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TealPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Online Medicine Delivery & Distribution Memo",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            DeliveryStatusBadge(statusStr = order.status)
                        }
                    }

                    // Metadata Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("চালান নং: ${order.invoiceNumber}", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text("তারিখ: ${Formatter.formatDate(order.orderDate)}", color = Color(0xFF64748B), fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${order.shopName} (${order.shopPhone})",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                Text(
                                    text = "ঠিকানা: ${order.shopAddress}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(start = 22.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF1B4965), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "রাইডার: ${order.riderName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF1B4965)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ডেলিভারি: ${order.deliveryTime ?: order.assignedTime}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF047857),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Medicine Items Table Header
                    item {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("প্রোডাক্ট / ঔষধ", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                                Text("প্যাক", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                                Text("পরিমাণ", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center, color = Color(0xFF1E3A8A))
                                Text("দর", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End, color = Color(0xFF1E3A8A))
                                Text("মোট", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End, color = Color(0xFF1E3A8A))
                            }
                        }
                    }

                    // Medicine Item Rows
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.productName, modifier = Modifier.weight(2f), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF0F172A))
                            Text(item.packType, modifier = Modifier.weight(1f), fontSize = 11.sp, color = Color(0xFF64748B))
                            Text("${item.quantity}", modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.Center, color = Color(0xFF0F172A))
                            Text("৳${item.unitPrice.toInt()}", modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.End, color = Color(0xFF64748B))
                            Text("৳${item.totalPrice.toInt()}", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, color = Color(0xFF0F172A))
                        }
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                    }

                    // Returns Section (if any)
                    if (returnItems.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "↩️ ফেরত ঔষধ তালিকা (Returned Products):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFBE123C)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    returnItems.forEach { ret ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "• ${ret.productName} (${ret.returnedQuantity} ${ret.packType} ফেরত - ${ret.returnReason})",
                                                fontSize = 11.sp,
                                                color = Color(0xFF9F1239),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "- ৳${ret.returnAmount.toInt()}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFBE123C)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Calculation Summary Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("মূল ইনভয়েস মূল্য (Gross Total):", fontSize = 12.sp, color = Color(0xFF475569))
                                    Text(Formatter.formatCurrency(order.grossAmount), fontWeight = FontWeight.Bold)
                                }

                                if (order.returnAmount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("ফেরত পণ্যের মূল্য (Returns Deducted):", fontSize = 12.sp, color = Color(0xFFE11D48))
                                        Text("- ${Formatter.formatCurrency(order.returnAmount)}", fontWeight = FontWeight.Bold, color = Color(0xFFE11D48))
                                    }
                                }

                                if (order.discount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("বিশেষ ছাড় (Discount):", fontSize = 12.sp, color = Color(0xFF475569))
                                        Text("- ${Formatter.formatCurrency(order.discount)}", fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFCBD5E1))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("সর্বমোট চূড়ান্ত বিল (Net Payable):", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Text(Formatter.formatCurrency(order.netAmount), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TealPrimary)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("পরিশোধিত নগদ টাকা (Paid Cash):", fontSize = 12.sp, color = Color(0xFF047857))
                                    Text(Formatter.formatCurrency(order.paidAmount), fontWeight = FontWeight.Bold, color = Color(0xFF047857))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("বকেয়া বাকি টাকা (Due Balance):", fontSize = 12.sp, color = if (order.dueAmount > 0) Color(0xFFB45309) else Color(0xFF64748B))
                                    Text(
                                        Formatter.formatCurrency(order.dueAmount),
                                        fontWeight = FontWeight.Bold,
                                        color = if (order.dueAmount > 0) Color(0xFFB45309) else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Share & Close Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("বন্ধ করুন")
                    }

                    Button(
                        onClick = {
                            shareInvoiceSlip(context, orderWithItems)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("btn_share_invoice_slip")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("মেমো শেয়ার করুন (Share)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun shareInvoiceSlip(context: Context, orderWithItems: OrderWithItems) {
    val order = orderWithItems.order
    val items = orderWithItems.items
    val returnItems = items.filter { it.returnedQuantity > 0 }

    val sb = StringBuilder()
    sb.append("📋 *MEDISALE BD MEMO*\n")
    sb.append("----------------------------\n")
    sb.append("চালান নং: ${order.invoiceNumber}\n")
    sb.append("তারিখ: ${Formatter.formatDate(order.orderDate)}\n")
    sb.append("দোকান: ${order.shopName}\n")
    sb.append("ঠিকানা: ${order.shopAddress}\n")
    sb.append("ফোন: ${order.shopPhone}\n")
    sb.append("ডেলিভারি ম্যান: ${order.riderName} (${order.riderPhone})\n")
    sb.append("ডেলিভারি সময়: ${order.deliveryTime ?: order.assignedTime}\n")
    sb.append("স্ট্যাটাস: ${order.status}\n\n")

    sb.append("📦 *ঔষধের বিবরণ:*\n")
    items.forEachIndexed { i, it ->
        sb.append("${i + 1}. ${it.productName} (${it.packType}) - ${it.quantity} x ৳${it.unitPrice.toInt()} = ৳${it.totalPrice.toInt()}\n")
    }

    if (returnItems.isNotEmpty()) {
        sb.append("\n↩️ *ফেরত ঔষধ:*\n")
        returnItems.forEach { ret ->
            sb.append("• ${ret.productName}: ${ret.returnedQuantity} ফেরত (${ret.returnReason}) = -৳${ret.returnAmount.toInt()}\n")
        }
    }

    sb.append("\n----------------------------\n")
    sb.append("মোট মূল্য: ৳${order.grossAmount.toInt()}\n")
    if (order.returnAmount > 0) sb.append("ফেরত বাদ: -৳${order.returnAmount.toInt()}\n")
    if (order.discount > 0) sb.append("ডিসকাউন্ট: -৳${order.discount.toInt()}\n")
    sb.append("চূড়ান্ত বিল: ৳${order.netAmount.toInt()}\n")
    sb.append("আদায়কৃত টাকা: ৳${order.paidAmount.toInt()}\n")
    sb.append("বকেয়া বাকি: ৳${order.dueAmount.toInt()}\n")
    sb.append("----------------------------\n")
    sb.append("ধন্যবাদ!")

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "ডেলিভারি মেমো শেয়ার করুন")
    context.startActivity(shareIntent)
}
