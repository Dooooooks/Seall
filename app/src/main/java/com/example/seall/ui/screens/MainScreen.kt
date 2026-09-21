package com.example.seall.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.seall.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seall.data.model.Order
import com.example.seall.ui.components.AppTab
import com.example.seall.ui.components.RapidEntryWizardModal
import com.example.seall.ui.components.SeallBottomBar
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: OrderViewModel,
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var orderToDelete by remember { mutableStateOf<Order?>(null) }

    val orders by viewModel.orders.collectAsState()
    val unpaidOrders by viewModel.unpaidOrders.collectAsState()
    val paidTotal by viewModel.paidTotal.collectAsState()
    val unpaidTotal by viewModel.unpaidTotal.collectAsState()
    val combinedTotal by viewModel.combinedTotal.collectAsState()

    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val paymentFilter by viewModel.paymentFilter.collectAsState()
    val selectedDateMillis by viewModel.selectedDateMillis.collectAsState()

    val isWizardOpen by viewModel.isWizardOpen.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()

    // Delete Confirmation Dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("Delete Order", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove the order for ${order.customerName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteOrder(order)
                        orderToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Rapid Entry 3-Step Wizard Modal
    RapidEntryWizardModal(
        isOpen = isWizardOpen,
        editingOrder = editingOrder,
        onDismiss = { viewModel.closeWizard() },
        onSubmit = { name, price, isPaid ->
            viewModel.submitOrder(name, price, isPaid)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.seal_icon),
                                contentDescription = "Seall Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Text(
                                    text = "Seall",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                val subtitle = when (currentTab) {
                                    AppTab.HOME -> null
                                    AppTab.DASHBOARD -> "Summary & Debtors"
                                    AppTab.CALENDAR -> "Timeline & Lookup"
                                }
                                if (subtitle != null) {
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Theme Toggle Icon Button
                        IconButton(
                            onClick = { onToggleTheme(!isDarkMode) },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Toggle Light/Dark Theme",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            SeallBottomBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HOME -> {
                    HomeTab(
                        orders = orders,
                        paidTotal = paidTotal,
                        unpaidTotal = unpaidTotal,
                        onOpenWizard = { viewModel.openWizard(null) },
                        onEditOrder = { order -> viewModel.openWizard(order) },
                        onTogglePaid = { order -> viewModel.togglePayment(order) },
                        onDeleteOrder = { order -> orderToDelete = order }
                    )
                }

                AppTab.DASHBOARD -> {
                    DashboardTab(
                        paidTotal = paidTotal,
                        unpaidTotal = unpaidTotal,
                        combinedTotal = combinedTotal,
                        unpaidOrders = unpaidOrders,
                        onMarkAsPaid = { order -> viewModel.togglePayment(order) }
                    )
                }

                AppTab.CALENDAR -> {
                    CalendarTab(
                        filteredOrders = filteredOrders,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        paymentFilter = paymentFilter,
                        onFilterChange = { viewModel.setPaymentFilter(it) },
                        selectedDateMillis = selectedDateMillis,
                        onDateSelected = { viewModel.setSelectedDateMillis(it) },
                        onTogglePaid = { order -> viewModel.togglePayment(order) },
                        onEditOrder = { order -> viewModel.openWizard(order) },
                        onDeleteOrder = { order -> orderToDelete = order }
                    )
                }
            }
        }
    }
}
