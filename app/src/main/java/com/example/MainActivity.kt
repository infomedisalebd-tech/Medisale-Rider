package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.OrderWithItems
import com.example.data.model.Rider
import com.example.data.model.Shop
import com.example.data.model.User
import com.example.ui.DeliveryViewModel
import com.example.ui.DashboardMetrics
import com.example.ui.DateFilterType
import com.example.ui.RiderSummary
import com.example.ui.RiderCustodySummary
import com.example.ui.dialogs.AddRiderDialog
import com.example.ui.dialogs.AddShopDialog
import com.example.ui.dialogs.AdminHandoverVerificationDialog
import com.example.ui.dialogs.ChangeRiderDialog
import com.example.ui.dialogs.ConfirmDeleteDialog
import com.example.ui.dialogs.CreateOrderDialog
import com.example.ui.dialogs.EditRiderAccountDialog
import com.example.ui.dialogs.EditRiderDialog
import com.example.ui.dialogs.EditShopDialog
import com.example.ui.dialogs.HandoverHistoryDialog
import com.example.ui.dialogs.InvoiceSlipDialog
import com.example.ui.dialogs.OrderSettlementDialog
import com.example.ui.dialogs.RiderHandoverRequestDialog
import com.example.ui.dialogs.SmartUploadInvoicesDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeliveriesScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RiderPortalScreen
import com.example.ui.screens.RidersScreen
import com.example.ui.screens.ShopsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.TealPrimary
import kotlinx.coroutines.launch

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("ড্যাশবোর্ড", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard"),
    DELIVERIES("চালান ও অর্ডার", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping, "tab_deliveries"),
    RIDERS("রাইডার ও আইডি", Icons.Filled.DirectionsBike, Icons.Outlined.DirectionsBike, "tab_riders"),
    SHOPS("দোকান", Icons.Filled.LocalPharmacy, Icons.Outlined.LocalPharmacy, "tab_shops")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: DeliveryViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Dialog visibility states
    var showCreateOrderDialog by remember { mutableStateOf(false) }
    var editingOrderWithItems by remember { mutableStateOf<OrderWithItems?>(null) }
    var showSmartUploadDialog by remember { mutableStateOf(false) }
    var changingRiderOrder by remember { mutableStateOf<OrderWithItems?>(null) }
    var settlementOrder by remember { mutableStateOf<OrderWithItems?>(null) }
    var invoiceSlipOrder by remember { mutableStateOf<OrderWithItems?>(null) }
    var showAddRiderDialog by remember { mutableStateOf(false) }
    var editingRider by remember { mutableStateOf<Rider?>(null) }
    var deletingRider by remember { mutableStateOf<Rider?>(null) }
    var editingRiderAccount by remember { mutableStateOf<Rider?>(null) }
    var showAddShopDialog by remember { mutableStateOf(false) }
    var editingShop by remember { mutableStateOf<Shop?>(null) }
    var deletingShop by remember { mutableStateOf<Shop?>(null) }
    var showRiderHandoverRequestDialog by remember { mutableStateOf(false) }
    var verifyingCustodySummary by remember { mutableStateOf<RiderCustodySummary?>(null) }
    var showHandoverHistoryDialog by remember { mutableStateOf(false) }

    // Observables
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val dateFilter by viewModel.selectedDateFilter.collectAsStateWithLifecycle()
    val customStartDate by viewModel.customStartDate.collectAsStateWithLifecycle()
    val customEndDate by viewModel.customEndDate.collectAsStateWithLifecycle()
    val selectedRiderFilterId by viewModel.selectedRiderFilterId.collectAsStateWithLifecycle()
    val filteredOrders by viewModel.filteredOrders.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val currentRiderOrders by viewModel.currentRiderOrders.collectAsStateWithLifecycle()
    val allRiders by viewModel.allRiders.collectAsStateWithLifecycle()
    val allShops by viewModel.allShops.collectAsStateWithLifecycle()
    val riderSummaries by viewModel.riderSummaries.collectAsStateWithLifecycle()
    val riderCustodySummaries by viewModel.riderCustodySummaries.collectAsStateWithLifecycle()
    val allHandoverLogs by viewModel.allHandoverLogs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val isRememberMe by viewModel.isRememberMe.collectAsStateWithLifecycle()
    val savedIdentifier by viewModel.savedIdentifier.collectAsStateWithLifecycle()
    val savedPassword by viewModel.savedPassword.collectAsStateWithLifecycle()

    // 1. If not logged in, show Login Screen
    if (currentUser == null) {
        LoginScreen(
            errorMessage = loginError,
            initialIdentifier = savedIdentifier,
            initialPassword = savedPassword,
            initialRememberMe = isRememberMe,
            onLogin = { identifier, pass, rememberMe ->
                viewModel.login(identifier, pass, rememberMe) {
                    scope.launch {
                        snackbarHostState.showSnackbar("সফলভাবে লগইন হয়েছে!")
                    }
                }
            },
            onClearError = { viewModel.clearLoginError() }
        )
        return
    }

    val user = currentUser!!

    // 2. If logged in as Rider, show Rider Portal Screen
    if (user.role == User.ROLE_RIDER) {
        val riderObj = allRiders.find { it.id == user.riderId }
        val currentRiderCustody = riderCustodySummaries.find { it.rider.id == user.riderId }

        RiderPortalScreen(
            user = user,
            rider = riderObj,
            orders = currentRiderOrders,
            custody = currentRiderCustody,
            onRequestHandover = { showRiderHandoverRequestDialog = true },
            onViewHandoverHistory = { showHandoverHistoryDialog = true },
            onOpenSettlement = { settlementOrder = it },
            onOpenInvoiceSlip = { invoiceSlipOrder = it },
            onLogout = {
                viewModel.logout()
                scope.launch { snackbarHostState.showSnackbar("লগআউট সম্পন্ন হয়েছে") }
            }
        )

        if (showRiderHandoverRequestDialog && currentRiderCustody != null) {
            RiderHandoverRequestDialog(
                custody = currentRiderCustody,
                onSubmit = { requestedCash, notes ->
                    viewModel.requestRiderHandover(user.riderId ?: 0L, requestedCash, notes)
                    showRiderHandoverRequestDialog = false
                    scope.launch {
                        snackbarHostState.showSnackbar("এডমিনের কাছে হ্যান্ডওভার রিকোয়েস্ট পাঠানো হয়েছে!")
                    }
                },
                onDismiss = { showRiderHandoverRequestDialog = false }
            )
        }

        if (showHandoverHistoryDialog) {
            val riderLogs = allHandoverLogs.filter { it.riderId == user.riderId }
            HandoverHistoryDialog(
                logs = riderLogs,
                onDismiss = { showHandoverHistoryDialog = false }
            )
        }

        settlementOrder?.let { orderWithItems ->
            OrderSettlementDialog(
                orderWithItems = orderWithItems,
                onDismiss = { settlementOrder = null },
                onConfirmSettlement = { newStatus, deliveryTime, paidAmount, paymentMethod, notes, updatedItems ->
                    viewModel.updateDeliveryStatusAndPayment(
                        order = orderWithItems.order,
                        items = updatedItems,
                        newStatus = newStatus,
                        deliveryTimeStr = deliveryTime,
                        paidAmount = paidAmount,
                        paymentMethod = paymentMethod,
                        notes = notes
                    )
                    settlementOrder = null
                    scope.launch { snackbarHostState.showSnackbar("ডেলিভারি ও রিটার্ন হিসাব ডাটাবেসে সেভ হয়েছে!") }
                }
            )
        }

        invoiceSlipOrder?.let { orderWithItems ->
            InvoiceSlipDialog(
                orderWithItems = orderWithItems,
                onDismiss = { invoiceSlipOrder = null }
            )
        }
        return
    }

    // 3. Admin View
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = NavySecondary,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
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
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("MEDISALE BD", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF16A34A),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("এডমিন প্যানেল", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text(user.email, fontSize = 11.sp, color = Color(0xFFCBD5E1))
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.syncToCloud { success, msg ->
                                    scope.launch { snackbarHostState.showSnackbar(msg) }
                                }
                            },
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), CircleShape)
                                .testTag("btn_cloud_sync")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Cloud Sync", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                viewModel.logout()
                                scope.launch { snackbarHostState.showSnackbar("এডমিন একাউন্ট থেকে লগআউট হয়েছে") }
                            },
                            modifier = Modifier
                                .background(Color(0x22FFFFFF), CircleShape)
                                .testTag("btn_admin_logout")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                AppTab.values().forEach { tab ->
                    val selected = selectedTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TealPrimary,
                            selectedTextColor = TealPrimary,
                            indicatorColor = Color(0xFFE0F4F5),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateOrderDialog = true },
                containerColor = TealPrimary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                modifier = Modifier.testTag("fab_create_order")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Delivery Order")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    AppTab.DASHBOARD -> {
                        DashboardScreen(
                            metrics = metrics,
                            selectedDateFilter = dateFilter,
                            onSelectDateFilter = { viewModel.setDateFilter(it) },
                            customStartDate = customStartDate,
                            customEndDate = customEndDate,
                            onSetCustomDateRange = { start, end -> viewModel.setCustomDateRange(start, end) },
                            riders = allRiders,
                            selectedRiderId = selectedRiderFilterId,
                            onSelectRider = { viewModel.setRiderFilter(it) },
                            riderSummaries = riderSummaries,
                            riderCustodySummaries = riderCustodySummaries,
                            onOpenCreateOrder = { showCreateOrderDialog = true },
                            onOpenSmartUpload = { showSmartUploadDialog = true },
                            onOpenAddRider = { showAddRiderDialog = true },
                            onOpenAddShop = { showAddShopDialog = true },
                            onOpenHandoverVerification = { custody -> verifyingCustodySummary = custody },
                            onOpenHandoverHistory = { showHandoverHistoryDialog = true },
                            onNavigateToDeliveries = { status ->
                                viewModel.setStatusFilter(status)
                                selectedTab = AppTab.DELIVERIES
                            },
                            onExportReport = { format ->
                                exportReport(context, format, metrics, dateFilter, riderSummaries)
                            }
                        )
                    }
                    AppTab.DELIVERIES -> {
                        DeliveriesScreen(
                            orders = filteredOrders,
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearch(it) },
                            selectedStatusFilter = statusFilter,
                            onSelectStatusFilter = { viewModel.setStatusFilter(it) },
                            selectedDateFilter = dateFilter,
                            onSelectDateFilter = { viewModel.setDateFilter(it) },
                            customStartDate = customStartDate,
                            customEndDate = customEndDate,
                            onSetCustomDateRange = { start, end -> viewModel.setCustomDateRange(start, end) },
                            onOpenCreateOrder = { showCreateOrderDialog = true },
                            onOpenSmartUpload = { showSmartUploadDialog = true },
                            onChangeRider = { changingRiderOrder = it },
                            onOpenSettlement = { settlementOrder = it },
                            onOpenInvoiceSlip = { invoiceSlipOrder = it },
                            onEditOrder = { editingOrderWithItems = it },
                            onDeleteOrder = { orderId ->
                                viewModel.deleteOrder(orderId)
                                scope.launch { snackbarHostState.showSnackbar("চালান মুছে ফেলা হয়েছে") }
                            }
                        )
                    }
                    AppTab.RIDERS -> {
                        RidersScreen(
                            riders = allRiders,
                            riderSummaries = riderSummaries,
                            selectedDateFilter = dateFilter,
                            onSelectDateFilter = { viewModel.setDateFilter(it) },
                            customStartDate = customStartDate,
                            customEndDate = customEndDate,
                            onSetCustomDateRange = { start, end -> viewModel.setCustomDateRange(start, end) },
                            onOpenAddRider = { showAddRiderDialog = true },
                            onEditRider = { rider ->
                                editingRider = rider
                            },
                            onEditRiderAccount = { rider ->
                                editingRiderAccount = rider
                            },
                            onDeleteRider = { rider ->
                                deletingRider = rider
                            },
                            onFilterRiderInDeliveries = { riderId ->
                                viewModel.setRiderFilter(riderId)
                                selectedTab = AppTab.DELIVERIES
                            }
                        )
                    }
                    AppTab.SHOPS -> {
                        ShopsScreen(
                            shops = allShops,
                            orders = filteredOrders,
                            selectedDateFilter = dateFilter,
                            onSelectDateFilter = { viewModel.setDateFilter(it) },
                            customStartDate = customStartDate,
                            customEndDate = customEndDate,
                            onSetCustomDateRange = { start, end -> viewModel.setCustomDateRange(start, end) },
                            onOpenAddShop = { showAddShopDialog = true },
                            onEditShop = { shop ->
                                editingShop = shop
                            },
                            onDeleteShop = { shop ->
                                deletingShop = shop
                            }
                        )
                    }
                }
            }
        }

        // --- Dialogs ---

        if (showSmartUploadDialog) {
            SmartUploadInvoicesDialog(
                shops = allShops,
                riders = allRiders,
                onDismiss = { showSmartUploadDialog = false },
                onSaveBatchOrders = { batchOrders, newShops ->
                    viewModel.createBulkOrders(batchOrders, newShops)
                    showSmartUploadDialog = false
                    scope.launch { snackbarHostState.showSnackbar("🎉 ${batchOrders.size}টি চালান সফলভাবে ডিসপ্যাচ ও সেভ হয়েছে!") }
                }
            )
        }

        changingRiderOrder?.let { orderWithItems ->
            ChangeRiderDialog(
                orderWithItems = orderWithItems,
                riders = allRiders,
                onDismiss = { changingRiderOrder = null },
                onConfirmChangeRider = { newRider ->
                    viewModel.reassignRider(orderWithItems.order, orderWithItems.items, newRider)
                    changingRiderOrder = null
                    scope.launch { snackbarHostState.showSnackbar("চালান #${orderWithItems.order.invoiceNumber} এর রাইডার বদলে ${newRider.name} করা হয়েছে") }
                }
            )
        }

        if (showCreateOrderDialog || editingOrderWithItems != null) {
            CreateOrderDialog(
                shops = allShops,
                riders = allRiders,
                editingOrder = editingOrderWithItems?.order,
                editingItems = editingOrderWithItems?.items.orEmpty(),
                onDismiss = {
                    showCreateOrderDialog = false
                    editingOrderWithItems = null
                },
                onSaveOrder = { order, items ->
                    if (editingOrderWithItems != null) {
                        viewModel.updateOrder(order, items)
                        scope.launch { snackbarHostState.showSnackbar("চালান সফলভাবে আপডেট করা হয়েছে!") }
                    } else {
                        viewModel.createOrder(order, items)
                        scope.launch { snackbarHostState.showSnackbar("নতুন ডেলিভারি চালান যোগ হয়েছে!") }
                    }
                    showCreateOrderDialog = false
                    editingOrderWithItems = null
                }
            )
        }

        settlementOrder?.let { orderWithItems ->
            OrderSettlementDialog(
                orderWithItems = orderWithItems,
                onDismiss = { settlementOrder = null },
                onConfirmSettlement = { newStatus, deliveryTime, paidAmount, paymentMethod, notes, updatedItems ->
                    viewModel.updateDeliveryStatusAndPayment(
                        order = orderWithItems.order,
                        items = updatedItems,
                        newStatus = newStatus,
                        deliveryTimeStr = deliveryTime,
                        paidAmount = paidAmount,
                        paymentMethod = paymentMethod,
                        notes = notes
                    )
                    settlementOrder = null
                    scope.launch { snackbarHostState.showSnackbar("ডেলিভারি ও রিটার্ন হিসাব আপডেট হয়েছে!") }
                }
            )
        }

        invoiceSlipOrder?.let { orderWithItems ->
            InvoiceSlipDialog(
                orderWithItems = orderWithItems,
                onDismiss = { invoiceSlipOrder = null }
            )
        }

        if (showAddRiderDialog) {
            AddRiderDialog(
                onDismiss = { showAddRiderDialog = false },
                onSaveRider = { name, phone, vehicle, zone, email, pass ->
                    viewModel.addRider(name, phone, vehicle, zone, email, pass)
                    showAddRiderDialog = false
                    scope.launch { snackbarHostState.showSnackbar("নতুন রাইডার ও লগইন আইডি তৈরি হয়েছে: $name") }
                }
            )
        }

        editingRider?.let { rider ->
            EditRiderDialog(
                rider = rider,
                onDismiss = { editingRider = null },
                onUpdateRider = { updatedRider ->
                    viewModel.updateRider(updatedRider)
                    editingRider = null
                    scope.launch { snackbarHostState.showSnackbar("${updatedRider.name} এর তথ্য সফলভাবে আপডেট হয়েছে!") }
                }
            )
        }

        deletingRider?.let { rider ->
            ConfirmDeleteDialog(
                title = "রাইডার মুছে ফেলা নিশ্চিতকরণ",
                message = "${rider.name} (মোবাইল: ${rider.phone}) কে ডিলিট করতে চান? এই রাইডারের লগইন আইডি ও মুছে যাবে।",
                onDismiss = { deletingRider = null },
                onConfirmDelete = {
                    viewModel.deleteRider(rider.id)
                    deletingRider = null
                    scope.launch { snackbarHostState.showSnackbar("${rider.name} মুছে ফেলা হয়েছে") }
                }
            )
        }

        editingRiderAccount?.let { rider ->
            EditRiderAccountDialog(
                riderName = rider.name,
                currentEmail = rider.loginEmail.ifBlank { "${rider.name.lowercase().replace(" ", "")}@medisale.com" },
                currentPass = rider.loginPassword.ifBlank { "123" },
                onDismiss = { editingRiderAccount = null },
                onSaveCredentials = { email, pass ->
                    viewModel.updateRiderPassword(rider, email, pass)
                    editingRiderAccount = null
                    scope.launch { snackbarHostState.showSnackbar("${rider.name} এর আইডি ও পাসওয়ার্ড আপডেট হয়েছে!") }
                }
            )
        }

        if (showAddShopDialog) {
            AddShopDialog(
                onDismiss = { showAddShopDialog = false },
                onSaveShop = { name, owner, phone, address, area ->
                    viewModel.addShop(name, owner, phone, address, area)
                    showAddShopDialog = false
                    scope.launch { snackbarHostState.showSnackbar("নতুন ফার্মেসি যুক্ত হয়েছে: $name") }
                }
            )
        }

        editingShop?.let { shop ->
            EditShopDialog(
                shop = shop,
                onDismiss = { editingShop = null },
                onUpdateShop = { updatedShop ->
                    viewModel.updateShop(updatedShop)
                    editingShop = null
                    scope.launch { snackbarHostState.showSnackbar("${updatedShop.name} এর তথ্য আপডেট হয়েছে!") }
                }
            )
        }

        deletingShop?.let { shop ->
            ConfirmDeleteDialog(
                title = "ফার্মেসি / দোকান মুছে ফেলা",
                message = "${shop.name} (${shop.area}) ফার্মেসিটি মুছে ফেলতে চান?",
                onDismiss = { deletingShop = null },
                onConfirmDelete = {
                    viewModel.deleteShop(shop.id)
                    deletingShop = null
                    scope.launch { snackbarHostState.showSnackbar("${shop.name} মুছে ফেলা হয়েছে") }
                }
            )
        }

        verifyingCustodySummary?.let { custody ->
            AdminHandoverVerificationDialog(
                custody = custody,
                onConfirmReceived = { cashReceived, returnOrderIds, cancelOrderIds, adminNote ->
                    viewModel.settleRiderHandover(
                        rider = custody.rider,
                        cashReceivedByAdmin = cashReceived,
                        receivedReturnOrderIds = returnOrderIds,
                        receivedCancelOrderIds = cancelOrderIds,
                        adminNotes = adminNote
                    )
                    verifyingCustodySummary = null
                    scope.launch {
                        snackbarHostState.showSnackbar("রাইডার ${custody.rider.name} এর ক্যাশ ও প্রোডাক্ট জমা কনফার্ম করা হয়েছে!")
                    }
                },
                onDismiss = { verifyingCustodySummary = null }
            )
        }

        if (showHandoverHistoryDialog) {
            HandoverHistoryDialog(
                logs = allHandoverLogs,
                onDismiss = { showHandoverHistoryDialog = false }
            )
        }
    }
}

fun exportReport(
    context: android.content.Context,
    format: String,
    metrics: DashboardMetrics,
    dateFilter: DateFilterType,
    riders: List<RiderSummary>
) {
    val reportText = buildString {
        appendLine("=== MEDISALE BD DELIVERY REPORT ===")
        appendLine("Date Filter: ${dateFilter.label}")
        appendLine("Generated At: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}")
        appendLine("-----------------------------------")
        appendLine("Total Orders: ${metrics.totalOrdersCount}")
        appendLine("Gross Amount: ${metrics.totalGrossAmount}")
        appendLine("Delivered Orders: ${metrics.deliveredCount} (${metrics.deliveredAmount})")
        appendLine("Pending Orders: ${metrics.pendingCount} (${metrics.pendingAmount})")
        appendLine("Return Orders: ${metrics.returnCount} (${metrics.totalReturnAmount})")
        appendLine("Cancelled Orders: ${metrics.cancelledCount}")
        appendLine("Total Collected Cash: ${metrics.totalCollectedAmount}")
        appendLine("Total Due: ${metrics.totalDueAmount}")
        appendLine("Delivery Completion Rate: ${String.format(java.util.Locale.US, "%.1f", metrics.deliveryCompletionRate)}%")
        appendLine("Collection Rate: ${String.format(java.util.Locale.US, "%.1f", metrics.collectionRate)}%")
        appendLine("-----------------------------------")
        appendLine("Rider Summaries:")
        riders.forEach { r ->
            appendLine("- ${r.rider.name} (${r.rider.zone}): Assigned=${r.totalAssigned}, Delivered=${r.deliveredCount}, Collected=${r.cashCollected}")
        }
    }

    try {
        val fileName = if (format == "PDF") "medisale_report_${System.currentTimeMillis()}.html" else "medisale_report_${System.currentTimeMillis()}.csv"
        val file = java.io.File(context.cacheDir, fileName)
        if (format == "PDF") {
            file.writeText("<html><head><meta charset='utf-8'><title>MediSale Report</title></head><body style='font-family:sans-serif;padding:20px;'><h2>MediSale BD Delivery Report</h2><pre>$reportText</pre></body></html>")
        } else {
            file.writeText(reportText)
        }

        val uri = androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = if (format == "PDF") "text/html" else "text/csv"
            putExtra(android.content.Intent.EXTRA_STREAM, uri)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "MediSale Delivery Report (${dateFilter.label})")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Export Report via"))
    } catch (e: Exception) {
        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_TEXT, reportText)
            putExtra(android.content.Intent.EXTRA_SUBJECT, "MediSale Delivery Report")
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Export Report via"))
    }
}

