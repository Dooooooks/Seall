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
import com.example.seall.ui.components.QuickEditDialog
import com.example.seall.ui.components.RapidEntryWizardModal
import com.example.seall.ui.components.SeallBottomBar
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.viewmodel.OrderViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: OrderViewModel,
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var orderToDelete by remember { mutableStateOf<Order?>(null) }
    var orderForNameEdit by remember { mutableStateOf<Order?>(null) }
    var orderForPriceEdit by remember { mutableStateOf<Order?>(null) }

    val orders by viewModel.orders.collectAsState()
    val paidTotal by viewModel.paidTotal.collectAsState()
    val unpaidTotal by viewModel.unpaidTotal.collectAsState()

    val isWizardOpen by viewModel.isWizardOpen.collectAsState()
    val editingOrder by viewModel.editingOrder.collectAsState()
    val stocks by viewModel.stocks.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val stockIngredients by viewModel.stockIngredients.collectAsState()

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
        stocks = stocks,
        onDismiss = { viewModel.closeWizard() },
        onSubmit = { name, price, isPaid, itemsSummary, itemsJson, totalItemCount ->
            viewModel.submitOrder(name, price, isPaid, itemsSummary, itemsJson, totalItemCount)
        }
    )

    // Quick Edit Name Dialog
    orderForNameEdit?.let { order ->
        QuickEditDialog(
            title = "Edit Customer Name",
            initialValue = order.customerName,
            label = "Customer Name",
            isNumeric = false,
            onDismiss = { orderForNameEdit = null },
            onConfirm = { newName ->
                viewModel.updateCustomerName(order, newName)
                orderForNameEdit = null
            }
        )
    }

    // Quick Edit Price Dialog
    orderForPriceEdit?.let { order ->
        QuickEditDialog(
            title = "Edit Price",
            initialValue = String.format(Locale.US, "%.2f", order.price),
            label = "Price in ₱",
            isNumeric = true,
            prefix = "₱",
            stocks = stocks,
            onDismiss = { orderForPriceEdit = null },
            onConfirm = { newPriceStr ->
                val newPrice = newPriceStr.toDoubleOrNull() ?: order.price
                viewModel.updateOrderPrice(order, newPrice)
                orderForPriceEdit = null
            }
        )
    }

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
                            Text(
                                text = "Seall",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
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
                        onEditName = { order -> orderForNameEdit = order },
                        onEditPrice = { order -> orderForPriceEdit = order },
                        onTogglePaid = { order -> viewModel.togglePayment(order) },
                        onDeleteOrder = { order -> orderToDelete = order }
                    )
                }

                AppTab.DASHBOARD -> {
                    DashboardTab(
                        allOrders = orders,
                        allIngredients = ingredients,
                        onMarkAsPaid = { order -> viewModel.togglePayment(order) },
                        onImportOrders = { importedOrders ->
                            viewModel.importOrders(importedOrders) {}
                        }
                    )
                }

                AppTab.ARCHIVES -> {
                    ArchivesTab(
                        allOrders = orders,
                        allIngredients = ingredients,
                        onDeleteArchive = { startOfDay, endOfDay ->
                            viewModel.deleteOrdersForDay(startOfDay, endOfDay)
                        },
                        onTogglePaid = { order -> viewModel.togglePayment(order) }
                    )
                }

                AppTab.STOCKS -> {
                    StocksTab(
                        stocks = stocks,
                        stockIngredients = stockIngredients,
                        onAddStock = { name, price, quantity -> viewModel.addStock(name, price, quantity) },
                        onUpdateStock = { stock, name, price, quantity -> viewModel.updateStock(stock, name, price, quantity) },
                        onAdjustStockQuantity = { stock, delta -> viewModel.adjustStockQuantity(stock, delta) },
                        onSetStockQuantity = { stock, quantity -> viewModel.updateStockQuantity(stock, quantity) },
                        onDeleteStock = { stock -> viewModel.deleteStock(stock) },
                        onAddStockIngredient = { stockId, name, quantity, cost -> viewModel.addStockIngredient(stockId, name, quantity, cost) },
                        onUpdateStockIngredient = { ing, name, quantity, cost -> viewModel.updateStockIngredient(ing, name, quantity, cost) },
                        onDeleteStockIngredient = { ing -> viewModel.deleteStockIngredient(ing) }
                    )
                }
            }
        }
    }
}
