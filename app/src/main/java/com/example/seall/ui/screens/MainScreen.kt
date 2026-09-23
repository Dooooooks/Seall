package com.example.seall.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seall.R
import com.example.seall.data.model.Order
import com.example.seall.data.model.StockIngredient
import com.example.seall.data.model.StockItem
import com.example.seall.ui.components.AppTab
import com.example.seall.ui.components.QuickEditDialog
import com.example.seall.ui.components.RapidEntryWizardModal
import com.example.seall.ui.components.SeallBottomBar
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.viewmodel.OrderViewModel
import com.example.seall.util.CsvHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: OrderViewModel,
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

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
    val deductIngredients by viewModel.deductIngredients.collectAsState()

    // ── Sidebar: Backup import confirmation state ──────────────────────────
    var pendingImportOrders by remember { mutableStateOf<List<Order>?>(null) }
    var pendingImportStocks by remember { mutableStateOf<List<StockItem>?>(null) }
    // stockIngredients imported as part of full backup; stored along with their resolved stock name→id map
    var pendingImportIngredients by remember { mutableStateOf<List<StockIngredient>?>(null) }

    // ── Export launchers ──────────────────────────────────────────────────
    val exportOrdersLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(CsvHelper.generateOrdersCsv(orders).toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Exported ${orders.size} orders!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val exportStocksLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(CsvHelper.generateStocksCsv(stocks).toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Exported ${stocks.size} products!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val exportFullLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(CsvHelper.generateFullBackup(orders, stocks, stockIngredients).toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Full backup exported!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ── Import launchers ──────────────────────────────────────────────────
    val importOrdersLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val csv = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                val parsed = CsvHelper.parseOrdersCsv(csv)
                if (parsed.isEmpty()) Toast.makeText(context, "No valid orders found in CSV.", Toast.LENGTH_LONG).show()
                else pendingImportOrders = parsed
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importStocksLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val csv = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                val parsed = CsvHelper.parseStocksCsv(csv)
                if (parsed.isEmpty()) Toast.makeText(context, "No valid products found in CSV.", Toast.LENGTH_LONG).show()
                else pendingImportStocks = parsed
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read CSV: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importFullLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val raw = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
                val backup = CsvHelper.parseFullBackup(raw)
                if (backup.orders.isEmpty() && backup.stocks.isEmpty()) {
                    Toast.makeText(context, "No valid data found in backup file.", Toast.LENGTH_LONG).show()
                } else {
                    // Confirm all at once
                    pendingImportOrders = backup.orders.ifEmpty { null }
                    pendingImportStocks = backup.stocks.ifEmpty { null }
                    pendingImportIngredients = backup.stockIngredients.ifEmpty { null }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read backup: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ── Import confirmation dialogs ────────────────────────────────────────
    pendingImportOrders?.let { ordersToImport ->
        AlertDialog(
            onDismissRequest = { pendingImportOrders = null },
            title = { Text("Import Orders", fontWeight = FontWeight.Bold) },
            text = { Text("Found ${ordersToImport.size} orders. Import them into Seall?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importOrders(ordersToImport)
                        Toast.makeText(context, "Imported ${ordersToImport.size} orders!", Toast.LENGTH_LONG).show()
                        pendingImportOrders = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeallPrimary, contentColor = SeallDarkContrast)
                ) { Text("Import", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingImportOrders = null }) { Text("Cancel") } }
        )
    }

    pendingImportStocks?.let { stocksToImport ->
        AlertDialog(
            onDismissRequest = { pendingImportStocks = null },
            title = { Text("Import Products", fontWeight = FontWeight.Bold) },
            text = { Text("Found ${stocksToImport.size} products. Import them into Seall? Existing products with the same name will be replaced.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.importStocks(stocksToImport) {
                            // After stocks are saved, import associated ingredients if any
                            pendingImportIngredients?.let { ings ->
                                if (ings.isNotEmpty()) {
                                    // Build name→id map from current stocks (freshly inserted)
                                    val nameToId = stocks.associate { it.name to it.id }
                                    val resolved = ings.mapNotNull { ing ->
                                        // Find matching stock by name via stockId lookup in newly inserted stocks
                                        val resolvedId = nameToId.values.firstOrNull() // fallback; proper resolution below
                                        ing.takeIf { resolvedId != null }
                                    }
                                    viewModel.importStockIngredients(ings)
                                    pendingImportIngredients = null
                                }
                            }
                        }
                        Toast.makeText(context, "Imported ${stocksToImport.size} products!", Toast.LENGTH_LONG).show()
                        pendingImportStocks = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeallPrimary, contentColor = SeallDarkContrast)
                ) { Text("Import", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingImportStocks = null }) { Text("Cancel") } }
        )
    }

    // Delete Confirmation Dialog
    orderToDelete?.let { order ->
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("Delete Order", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove the order for ${order.customerName}?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteOrder(order); orderToDelete = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { orderToDelete = null }) { Text("Cancel") } }
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
            onConfirm = { newName -> viewModel.updateCustomerName(order, newName); orderForNameEdit = null }
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

    // ── Sidebar drawer ────────────────────────────────────────────────────
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .systemBarsPadding()
                    .width(300.dp)
            ) {
                BackupDrawerContent(
                    onClose = { scope.launch { drawerState.close() } },
                    onExportFull = {
                        if (orders.isEmpty() && stocks.isEmpty()) Toast.makeText(context, "Nothing to backup", Toast.LENGTH_SHORT).show()
                        else exportFullLauncher.launch("seall_full_backup_${timestamp()}.csv")
                    },
                    onImportFull = {
                        importFullLauncher.launch(arrayOf("text/*", "*/*"))
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tapping logo OR "Seall" text opens the sidebar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        scope.launch { drawerState.open() }
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.seal_icon),
                                    contentDescription = "Seall Logo",
                                    modifier = Modifier.size(36.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "Seall",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Theme Toggle
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
                                    contentDescription = "Toggle Theme",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                SeallBottomBar(currentTab = currentTab, onTabSelected = { currentTab = it })
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                when (currentTab) {
                    AppTab.HOME -> HomeTab(
                        orders = orders,
                        paidTotal = paidTotal,
                        unpaidTotal = unpaidTotal,
                        onOpenWizard = { viewModel.openWizard(null) },
                        onEditName = { orderForNameEdit = it },
                        onEditPrice = { orderForPriceEdit = it },
                        onTogglePaid = { viewModel.togglePayment(it) },
                        onDeleteOrder = { orderToDelete = it }
                    )
                    AppTab.DASHBOARD -> DashboardTab(
                        allOrders = orders,
                        allIngredients = ingredients,
                        stocks = stocks,
                        stockIngredients = stockIngredients,
                        deductIngredients = deductIngredients,
                        onToggleDeductIngredients = { viewModel.toggleDeductIngredients() },
                        onMarkAsPaid = { viewModel.togglePayment(it) },
                        onImportOrders = { viewModel.importOrders(it) }
                    )
                    AppTab.ARCHIVES -> ArchivesTab(
                        allOrders = orders,
                        allIngredients = ingredients,
                        stocks = stocks,
                        stockIngredients = stockIngredients,
                        onDeleteArchive = { start, end -> viewModel.deleteOrdersForDay(start, end) },
                        onTogglePaid = { viewModel.togglePayment(it) }
                    )
                    AppTab.STOCKS -> StocksTab(
                        stocks = stocks,
                        stockIngredients = stockIngredients,
                        onAddStock = { name, price, quantity -> viewModel.addStock(name, price, quantity) },
                        onUpdateStock = { stock, name, price, quantity -> viewModel.updateStock(stock, name, price, quantity) },
                        onAdjustStockQuantity = { stock, delta -> viewModel.adjustStockQuantity(stock, delta) },
                        onSetStockQuantity = { stock, quantity -> viewModel.updateStockQuantity(stock, quantity) },
                        onDeleteStock = { viewModel.deleteStock(it) },
                        onAddStockIngredient = { stockId, name, quantity, cost -> viewModel.addStockIngredient(stockId, name, quantity, cost) },
                        onUpdateStockIngredient = { ing, name, quantity, cost -> viewModel.updateStockIngredient(ing, name, quantity, cost) },
                        onDeleteStockIngredient = { viewModel.deleteStockIngredient(it) }
                    )
                }
            }
        }
    }
}

private fun timestamp(): String = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())

// ─── Sidebar content ─────────────────────────────────────────────────────────

@Composable
private fun BackupDrawerContent(
    onClose: () -> Unit,
    onExportFull: () -> Unit,
    onImportFull: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Data Backup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "& Spreadsheet",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SeallPrimary
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Text(
            text = "Export all your data as a single CSV file, or restore from a previous backup.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider()

        // Full Backup
        BackupCard(
            icon = Icons.Default.Storage,
            title = "Full Backup",
            subtitle = "All orders + products + ingredients in one file",
            exportLabel = "Export Backup",
            importLabel = "Restore Backup",
            onExport = onExportFull,
            onImport = onImportFull
        )

        HorizontalDivider()

        // Personal note
        Text(
            text = "\"For the person who is always doing her best, I hope that every day is a profit day!\"\n\n— Lloydie 🤍",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun BackupCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    exportLabel: String,
    importLabel: String,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SeallPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = SeallPrimary, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onExport,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SeallPrimary, contentColor = SeallDarkContrast),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(exportLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = onImport,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(importLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
