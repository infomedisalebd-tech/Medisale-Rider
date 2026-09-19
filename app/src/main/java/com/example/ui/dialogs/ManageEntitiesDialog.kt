package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.Badge
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TealPrimaryLight

/**
 * Compact, modern, and space-efficient text field designed for dialogs.
 */
@Composable
private fun CompactInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    testTag: String = ""
) {
    var passwordVisible by remember { mutableStateOf(!isPassword) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp, color = Color(0xFF475569)) },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, fontSize = 11.5.sp, color = Color(0xFF94A3B8)) }
        } else null,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(16.dp)
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        shape = RoundedCornerShape(9.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF8FAFC),
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedLabelColor = TealPrimary,
            cursorColor = TealPrimary
        ),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F172A)
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    )
}

/**
 * Modern compact Header for entity dialogs
 */
@Composable
private fun CompactDialogHeader(
    title: String,
    subtitle: String,
    badgeIcon: ImageVector,
    badgeColor: Color,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color(0xFF475569),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Modern compact Bottom Action Buttons
 */
@Composable
private fun CompactDialogActions(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: String = "সংরক্ষণ করুন",
    confirmIcon: ImageVector = Icons.Default.Check,
    confirmColor: Color = TealPrimary,
    confirmTag: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        ) {
            Text("বাতিল", fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
            modifier = Modifier
                .weight(1.3f)
                .height(38.dp)
                .then(if (confirmTag.isNotEmpty()) Modifier.testTag(confirmTag) else Modifier)
        ) {
            Icon(imageVector = confirmIcon, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(5.dp))
            Text(confirmText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// 1. ADD RIDER DIALOG (Small, Modern UI/UX)
// -------------------------------------------------------------
@Composable
fun AddRiderDialog(
    onDismiss: () -> Unit,
    onSaveRider: (name: String, phone: String, vehicle: String, zone: String, email: String, pass: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("Motorcycle") }
    var zone by remember { mutableStateOf("ঢাকা (Dhaka)") }
    var loginEmail by remember { mutableStateOf("") }
    var loginPass by remember { mutableStateOf("123456") }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 580.dp)
                .testTag("add_rider_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactDialogHeader(
                    title = "নতুন রাইডার যোগ ও অ্যাকাউন্ট",
                    subtitle = "রাইডার তথ্য এবং লগইন আইডি ডাটাবেসে সেভ হবে",
                    badgeIcon = Icons.Default.DirectionsBike,
                    badgeColor = TealPrimary,
                    onDismiss = onDismiss
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                // Rider Information Grid
                CompactInputField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (loginEmail.isBlank() && it.isNotBlank()) {
                            val cleanName = it.trim().lowercase().replace("\\s+".toRegex(), "")
                            loginEmail = "${cleanName}@medisale.com"
                        }
                    },
                    label = "রাইডারের নাম (Full Name)*",
                    placeholder = "উদাঃ মোঃ রফিকুল ইসলাম",
                    leadingIcon = Icons.Default.Person,
                    testTag = "input_rider_name"
                )

                CompactInputField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "মোবাইল নম্বর (Phone)*",
                    placeholder = "017XXXXXXXX",
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = Icons.Default.Phone,
                    testTag = "input_rider_phone"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactInputField(
                        value = vehicle,
                        onValueChange = { vehicle = it },
                        label = "যানবাহন (Vehicle)",
                        placeholder = "Motorcycle / Cycle",
                        leadingIcon = Icons.Default.DirectionsBike,
                        modifier = Modifier.weight(1f),
                        testTag = "input_rider_vehicle"
                    )

                    CompactInputField(
                        value = zone,
                        onValueChange = { zone = it },
                        label = "এলাকা (Zone)",
                        placeholder = "উদাঃ ধানমন্ডি, মিরপুর",
                        leadingIcon = Icons.Default.Place,
                        modifier = Modifier.weight(1f),
                        testTag = "input_rider_zone"
                    )
                }

                // Compact Modern Credentials Card
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "রাইডার লগইন ক্রেডেনশিয়াল (Login Access)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }

                        CompactInputField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = "লগইন ইমেইল / ইউজার আইডি",
                            leadingIcon = Icons.Default.Person,
                            testTag = "input_rider_login_email"
                        )

                        CompactInputField(
                            value = loginPass,
                            onValueChange = { loginPass = it },
                            label = "লগইন পাসওয়ার্ড (Password)",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true,
                            testTag = "input_rider_login_pass"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                CompactDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (name.isNotBlank()) {
                            onSaveRider(
                                name.trim(),
                                phone.trim(),
                                vehicle.trim(),
                                zone.trim(),
                                loginEmail.trim(),
                                loginPass.trim()
                            )
                        }
                    },
                    confirmText = "রাইডার সেভ করুন",
                    confirmTag = "btn_confirm_add_rider"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. EDIT RIDER DIALOG (Small, Modern UI/UX)
// -------------------------------------------------------------
@Composable
fun EditRiderDialog(
    rider: Rider,
    onDismiss: () -> Unit,
    onUpdateRider: (rider: Rider) -> Unit
) {
    var name by remember { mutableStateOf(rider.name) }
    var phone by remember { mutableStateOf(rider.phone) }
    var vehicle by remember { mutableStateOf(rider.vehicleType) }
    var zone by remember { mutableStateOf(rider.zone) }
    var loginEmail by remember { mutableStateOf(rider.loginEmail) }
    var loginPass by remember { mutableStateOf(rider.loginPassword) }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 580.dp)
                .testTag("edit_rider_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactDialogHeader(
                    title = "রাইডারের তথ্য এডিট করুন",
                    subtitle = "নাম, ফোন, জোন ও লগইন তথ্য সংশোধন",
                    badgeIcon = Icons.Default.Edit,
                    badgeColor = TealPrimary,
                    onDismiss = onDismiss
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                CompactInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "রাইডারের নাম (Full Name)*",
                    leadingIcon = Icons.Default.Person,
                    testTag = "input_edit_rider_name"
                )

                CompactInputField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "মোবাইল নম্বর (Phone)*",
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = Icons.Default.Phone,
                    testTag = "input_edit_rider_phone"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactInputField(
                        value = vehicle,
                        onValueChange = { vehicle = it },
                        label = "যানবাহন (Vehicle)",
                        leadingIcon = Icons.Default.DirectionsBike,
                        modifier = Modifier.weight(1f)
                    )

                    CompactInputField(
                        value = zone,
                        onValueChange = { zone = it },
                        label = "ডেলিভারি এলাকা (Zone)",
                        leadingIcon = Icons.Default.Place,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Credentials Panel
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "লগইন আইডি ও পাসওয়ার্ড (Login Credentials)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155)
                            )
                        }

                        CompactInputField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = "লগইন ইমেইল / ইউজার আইডি",
                            leadingIcon = Icons.Default.Person
                        )

                        CompactInputField(
                            value = loginPass,
                            onValueChange = { loginPass = it },
                            label = "লগইন পাসওয়ার্ড (Password)",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                CompactDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (name.isNotBlank()) {
                            onUpdateRider(
                                rider.copy(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    vehicleType = vehicle.trim(),
                                    zone = zone.trim(),
                                    loginEmail = loginEmail.trim(),
                                    loginPassword = loginPass.trim()
                                )
                            )
                        }
                    },
                    confirmText = "আপডেট সংরক্ষণ",
                    confirmTag = "btn_save_edit_rider"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 3. EDIT RIDER ACCOUNT DIALOG (Credentials only)
// -------------------------------------------------------------
@Composable
fun EditRiderAccountDialog(
    riderName: String,
    currentEmail: String,
    currentPass: String,
    onDismiss: () -> Unit,
    onSaveCredentials: (email: String, pass: String) -> Unit
) {
    var email by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf(currentPass) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("edit_rider_account_dialog")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CompactDialogHeader(
                    title = "রাইডার লগইন আইডি পরিবর্তন",
                    subtitle = "রাইডার: $riderName",
                    badgeIcon = Icons.Default.VpnKey,
                    badgeColor = TealPrimary,
                    onDismiss = onDismiss
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                CompactInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "লগইন ইমেইল / আইডি",
                    leadingIcon = Icons.Default.Person
                )

                CompactInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "নতুন পাসওয়ার্ড (Password)",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                CompactDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            onSaveCredentials(email.trim(), password.trim())
                        }
                    },
                    confirmText = "পাসওয়ার্ড আপডেট"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. ADD SHOP DIALOG (Small, Modern UI/UX)
// -------------------------------------------------------------
@Composable
fun AddShopDialog(
    onDismiss: () -> Unit,
    onSaveShop: (name: String, owner: String, phone: String, address: String, area: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("ঢাকা (Dhaka)") }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 560.dp)
                .testTag("add_shop_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactDialogHeader(
                    title = "নতুন ফার্মেসি / দোকান যুক্ত করুন",
                    subtitle = "দোকানের নাম, ঠিকানা ও যোগাযোগের তথ্য সংরক্ষণ",
                    badgeIcon = Icons.Default.LocalPharmacy,
                    badgeColor = Color(0xFF0284C7),
                    onDismiss = onDismiss
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                CompactInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "ফার্মেসির নাম (Pharmacy Name)*",
                    placeholder = "উদাঃ নিউ ঢাকা ফার্মেসি",
                    leadingIcon = Icons.Default.LocalPharmacy,
                    testTag = "input_shop_name"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactInputField(
                        value = owner,
                        onValueChange = { owner = it },
                        label = "মালিক / ম্যানেজার",
                        placeholder = "নাম",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier.weight(1.1f),
                        testTag = "input_shop_owner"
                    )

                    CompactInputField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "ফোন নম্বর*",
                        placeholder = "01XXXXXXXXX",
                        keyboardType = KeyboardType.Phone,
                        leadingIcon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f),
                        testTag = "input_shop_phone"
                    )
                }

                CompactInputField(
                    value = address,
                    onValueChange = { address = it },
                    label = "ঠিকানা (Full Address)*",
                    placeholder = "দোকান নং, রোড, বাজার",
                    leadingIcon = Icons.Default.Place,
                    testTag = "input_shop_address"
                )

                CompactInputField(
                    value = area,
                    onValueChange = { area = it },
                    label = "এলাকা / থানা (Area / Thana)",
                    placeholder = "উদাঃ মিরপুর-১০, ঢাকা",
                    leadingIcon = Icons.Default.Storefront,
                    testTag = "input_shop_area"
                )

                Spacer(modifier = Modifier.height(4.dp))

                CompactDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (name.isNotBlank()) {
                            onSaveShop(
                                name.trim(),
                                owner.trim(),
                                phone.trim(),
                                address.trim(),
                                area.trim()
                            )
                        }
                    },
                    confirmText = "দোকান যুক্ত করুন",
                    confirmColor = Color(0xFF0284C7),
                    confirmTag = "btn_confirm_add_shop"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 5. EDIT SHOP DIALOG (Small, Modern UI/UX)
// -------------------------------------------------------------
@Composable
fun EditShopDialog(
    shop: Shop,
    onDismiss: () -> Unit,
    onUpdateShop: (shop: Shop) -> Unit
) {
    var name by remember { mutableStateOf(shop.name) }
    var owner by remember { mutableStateOf(shop.ownerOrManager) }
    var phone by remember { mutableStateOf(shop.phone) }
    var address by remember { mutableStateOf(shop.address) }
    var area by remember { mutableStateOf(shop.area) }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 560.dp)
                .testTag("edit_shop_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(14.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactDialogHeader(
                    title = "দোকানের তথ্য এডিট করুন",
                    subtitle = "ফার্মেসির নাম, ঠিকানা ও মোবাইল পরিবর্তন",
                    badgeIcon = Icons.Default.Edit,
                    badgeColor = Color(0xFF0284C7),
                    onDismiss = onDismiss
                )

                HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                CompactInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "ফার্মেসির নাম (Pharmacy Name)*",
                    leadingIcon = Icons.Default.LocalPharmacy,
                    testTag = "input_edit_shop_name"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CompactInputField(
                        value = owner,
                        onValueChange = { owner = it },
                        label = "মালিক / ম্যানেজার",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier.weight(1.1f)
                    )

                    CompactInputField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "মোবাইল নম্বর",
                        keyboardType = KeyboardType.Phone,
                        leadingIcon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f)
                    )
                }

                CompactInputField(
                    value = address,
                    onValueChange = { address = it },
                    label = "পূর্ণ ঠিকানা (Full Address)",
                    leadingIcon = Icons.Default.Place
                )

                CompactInputField(
                    value = area,
                    onValueChange = { area = it },
                    label = "এলাকা / থানা (Area)",
                    leadingIcon = Icons.Default.Storefront
                )

                Spacer(modifier = Modifier.height(4.dp))

                CompactDialogActions(
                    onDismiss = onDismiss,
                    onConfirm = {
                        if (name.isNotBlank()) {
                            onUpdateShop(
                                shop.copy(
                                    name = name.trim(),
                                    ownerOrManager = owner.trim(),
                                    phone = phone.trim(),
                                    address = address.trim(),
                                    area = area.trim()
                                )
                            )
                        }
                    },
                    confirmText = "আপডেট করুন",
                    confirmColor = Color(0xFF0284C7),
                    confirmTag = "btn_save_edit_shop"
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 6. CONFIRM DELETE DIALOG (Small, Modern UI/UX)
// -------------------------------------------------------------
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEE2E2)),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color(0xFFDC2626)
                    )
                }

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color(0xFF475569),
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("বাতিল", fontSize = 12.sp, color = Color(0xFF475569))
                    }

                    Button(
                        onClick = onConfirmDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("ডিলিট করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
