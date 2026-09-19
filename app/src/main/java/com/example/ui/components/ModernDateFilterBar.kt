package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DateFilterType
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Modern, ultra-compact and responsive Date-to-Date Filter Bar for Dashboard, Deliveries, Riders, and Shops.
 * Takes minimal vertical height (~36dp) while providing quick presets and an intuitive date-range picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDateFilterBar(
    selectedDateFilter: DateFilterType,
    onSelectDateFilter: (DateFilterType) -> Unit,
    customStartDate: Long?,
    customEndDate: Long?,
    onSetCustomRange: (Long?, Long?) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    var showRangePickerDialog by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val isCustomActive = selectedDateFilter == DateFilterType.CUSTOM && customStartDate != null && customEndDate != null

    val customDisplayText = remember(customStartDate, customEndDate) {
        if (customStartDate != null && customEndDate != null) {
            val startStr = dateFormat.format(Date(customStartDate))
            val endStr = dateFormat.format(Date(customEndDate))
            if (startStr == endStr) startStr else "$startStr - $endStr"
        } else {
            "তারিখ হতে তারিখ"
        }
    }

    Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 0.5.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("modern_date_filter_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Micro Badge: Icon & Optional Short Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(12.dp)
                )
                if (title != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title.take(8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Horizontally Scrollable Modern Micro-Pill Filters
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Presets: Today, Yesterday, This Week, This Month, All Time
                DateFilterType.values().forEach { filterType ->
                    if (filterType != DateFilterType.CUSTOM) {
                        val isSelected = selectedDateFilter == filterType

                        val animatedBg by animateColorAsState(
                            targetValue = if (isSelected) TealPrimary else Color(0xFFF8FAFC),
                            label = "filter_pill_bg"
                        )
                        val animatedTextColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else Color(0xFF475569),
                            label = "filter_pill_text"
                        )

                        Surface(
                            onClick = { onSelectDateFilter(filterType) },
                            shape = RoundedCornerShape(6.dp),
                            color = animatedBg,
                            border = BorderStroke(
                                0.8.dp,
                                if (isSelected) TealPrimary else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .height(27.dp)
                                .testTag("filter_chip_${filterType.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = filterType.label,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = animatedTextColor
                                )
                            }
                        }
                    }
                }

                // Date-to-Date Custom Range Pill
                Surface(
                    onClick = { showRangePickerDialog = true },
                    shape = RoundedCornerShape(6.dp),
                    color = if (isCustomActive) NavySecondary else Color(0xFFF0FDF4),
                    border = BorderStroke(
                        0.8.dp,
                        if (isCustomActive) NavySecondary else Color(0xFF86EFAC)
                    ),
                    modifier = Modifier
                        .height(27.dp)
                        .testTag("btn_custom_date_range_picker")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date to Date Range",
                            tint = if (isCustomActive) Color.White else Color(0xFF15803D),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (isCustomActive) "📅 $customDisplayText" else "📅 Date to Date",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomActive) Color.White else Color(0xFF166534)
                        )
                        if (isCustomActive) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .clickable {
                                        onSelectDateFilter(DateFilterType.TODAY)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Reset Date",
                                    tint = Color.White,
                                    modifier = Modifier.size(9.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Material 3 Responsive Date Range Picker Dialog
    if (showRangePickerDialog) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = customStartDate ?: System.currentTimeMillis(),
            initialSelectedEndDateMillis = customEndDate ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showRangePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis ?: start
                        if (start != null) {
                            onSetCustomRange(start, end)
                            onSelectDateFilter(DateFilterType.CUSTOM)
                        }
                        showRangePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("ফিল্টার প্রয়োগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRangePickerDialog = false }) {
                    Text("বাতিল", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Header with quick shortcuts
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅 নির্দিষ্ট তারিখ হতে তারিখ (Date Range)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                // Quick presets row inside dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val now = Calendar.getInstance()
                    
                    // Shortcut 7 days
                    Surface(
                        onClick = {
                            val end = now.timeInMillis
                            val startCal = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -6) }
                            onSetCustomRange(startCal.timeInMillis, end)
                            onSelectDateFilter(DateFilterType.CUSTOM)
                            showRangePickerDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text("গত ৭ দিন", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }

                    // Shortcut 30 days
                    Surface(
                        onClick = {
                            val end = now.timeInMillis
                            val startCal = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -29) }
                            onSetCustomRange(startCal.timeInMillis, end)
                            onSelectDateFilter(DateFilterType.CUSTOM)
                            showRangePickerDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text("গত ৩০ দিন", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }

                    // Shortcut This Month
                    Surface(
                        onClick = {
                            val startCal = (now.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
                            onSetCustomRange(startCal.timeInMillis, now.timeInMillis)
                            onSelectDateFilter(DateFilterType.CUSTOM)
                            showRangePickerDialog = false
                        },
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text("চলতি মাস", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f, fill = false),
                    title = null,
                    headline = null,
                    showModeToggle = false
                )
            }
        }
    }
}

