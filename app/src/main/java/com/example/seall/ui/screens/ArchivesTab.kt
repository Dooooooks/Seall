package com.example.seall.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seall.data.model.Ingredient
import com.example.seall.data.model.Order
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPaidGreen
import com.example.seall.ui.theme.SeallPaidGreenContainer
import com.example.seall.ui.theme.SeallPrimary
import com.example.seall.ui.theme.SeallUnpaidAmber
import com.example.seall.ui.theme.SeallUnpaidAmberContainer
import com.example.seall.util.DateUtils
import java.util.Locale

data class ArchivedDay(
    val startOfDay: Long,
    val endOfDay: Long,
    val dateLabel: String,
    val shortDate: String,
    val orders: List<Order>,
    val grossTotal: Double,
    val paidTotal: Double,
    val unpaidTotal: Double,
    val unpaidOrders: List<Order>,
    val ingredientsCost: Double = 0.0,
    val netEarnings: Double = grossTotal - ingredientsCost
)

@Composable
fun ArchivesTab(
    allOrders: List<Order>,
    allIngredients: List<Ingredient> = emptyList(),
    onDeleteArchive: (startOfDay: Long, endOfDay: Long) -> Unit,
    onTogglePaid: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedArchive by remember { mutableStateOf<ArchivedDay?>(null) }
    var archiveToDelete by remember { mutableStateOf<ArchivedDay?>(null) }

    val startOfToday = remember { DateUtils.getStartOfDay() }

    // Group past orders and ingredients into archived daily dashboards
    val archives = remember(allOrders, allIngredients) {
        val pastOrders = allOrders.filter { it.createdAt < startOfToday }
        val pastIngredients = allIngredients.filter { it.createdAt < startOfToday }
        val allDayStarts = (pastOrders.map { DateUtils.getStartOfDay(it.createdAt) } +
            pastIngredients.map { DateUtils.getStartOfDay(it.createdAt) }).distinct()

        allDayStarts.mapNotNull { dayStart ->
            val dayEnd = DateUtils.getEndOfDay(dayStart)
            val dayOrders = pastOrders.filter { it.createdAt in dayStart..dayEnd }
            val dayIngredients = pastIngredients.filter { it.createdAt in dayStart..dayEnd }
            val gross = dayOrders.sumOf { it.price }
            val ingredientsCost = dayIngredients.sumOf { it.price }

            if (dayOrders.isEmpty() && dayIngredients.isEmpty()) {
                null
            } else {
                val paid = dayOrders.filter { it.isPaid }.sumOf { it.price }
                val unpaid = dayOrders.filter { !it.isPaid }.sumOf { it.price }
                ArchivedDay(
                    startOfDay = dayStart,
                    endOfDay = dayEnd,
                    dateLabel = DateUtils.formatDate(dayStart),
                    shortDate = DateUtils.formatShortDate(dayStart),
                    orders = dayOrders.sortedByDescending { it.createdAt },
                    grossTotal = gross,
                    paidTotal = paid,
                    unpaidTotal = unpaid,
                    unpaidOrders = dayOrders.filter { !it.isPaid },
                    ingredientsCost = ingredientsCost,
                    netEarnings = gross - ingredientsCost
                )
            }
        }.sortedByDescending { it.startOfDay }
    }

    // Keep selectedArchive synced with updated orders
    val currentSelectedArchive = remember(selectedArchive, archives) {
        selectedArchive?.let { sel ->
            archives.find { it.startOfDay == sel.startOfDay }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (currentSelectedArchive != null) {
            // Detailed Dashboard View for the Selected Day
            ArchivedDayDetailView(
                archive = currentSelectedArchive,
                onBack = { selectedArchive = null },
                onDelete = { archiveToDelete = currentSelectedArchive },
                onTogglePaid = onTogglePaid
            )
        } else {
            // List of Archived Daily Dashboards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "Daily Archives",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Past daily dashboards and sales history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (archives.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(SeallPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null,
                                        tint = SeallPrimary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "No Archived Dashboards Yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "When tomorrow arrives, today's sales dashboard will automatically be saved here with its complete metrics and order records.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "PAST DAYS (${archives.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(archives, key = { it.startOfDay }) { archive ->
                        ArchivedDayCard(
                            archive = archive,
                            onClick = { selectedArchive = archive },
                            onDelete = { archiveToDelete = archive }
                        )
                    }
                }
            }
        }
    }

    // Delete Archive Confirmation Dialog
    archiveToDelete?.let { archive ->
        AlertDialog(
            onDismissRequest = { archiveToDelete = null },
            title = { Text("Delete Archived Dashboard", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to delete the dashboard for ${archive.dateLabel}? This will remove all ${archive.orders.size} transactions for this day.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteArchive(archive.startOfDay, archive.endOfDay)
                        if (selectedArchive?.startOfDay == archive.startOfDay) {
                            selectedArchive = null
                        }
                        archiveToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { archiveToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ArchivedDayCard(
    archive: ArchivedDay,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header: Date & Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = SeallPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = archive.dateLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Day's Archive",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Summary Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gross Total Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1.3f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "Gross Sales",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = String.format(Locale.US, "₱%.2f", archive.grossTotal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Paid Total Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SeallPaidGreenContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "Paid",
                            style = MaterialTheme.typography.labelSmall,
                            color = SeallPaidGreen
                        )
                        Text(
                            text = String.format(Locale.US, "₱%.2f", archive.paidTotal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SeallPaidGreen
                        )
                    }
                }

                // Unpaid Total Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SeallUnpaidAmberContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                        Text(
                            text = "Unpaid",
                            style = MaterialTheme.typography.labelSmall,
                            color = SeallUnpaidAmber
                        )
                        Text(
                            text = String.format(Locale.US, "₱%.2f", archive.unpaidTotal),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SeallUnpaidAmber
                        )
                    }
                }
            }

            if (archive.ingredientsCost > 0.0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Net: ${String.format(Locale.US, "₱%.2f", archive.netEarnings)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (archive.netEarnings >= 0) SeallPaidGreen else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Ingredients: -${String.format(Locale.US, "₱%.2f", archive.ingredientsCost)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Footer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${archive.orders.size} transactions total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tap to view full dashboard →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SeallPrimary
                )
            }
        }
    }
}

@Composable
private fun ArchivedDayDetailView(
    archive: ArchivedDay,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onTogglePaid: (Order) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("All Archives", fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Archive",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Title and Date
        item {
            Column {
                Text(
                    text = archive.dateLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Archived Dashboard Snapshot",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Hero Card: Combined Gross Sales
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (archive.ingredientsCost > 0.0) "Net Earnings" else "Gross Sales",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = String.format(Locale.US, "₱%.2f", if (archive.ingredientsCost > 0.0) archive.netEarnings else archive.grossTotal),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (archive.ingredientsCost > 0.0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Gross ₱${String.format(Locale.US, "%.2f", archive.grossTotal)} − Ingredients ₱${String.format(Locale.US, "%.2f", archive.ingredientsCost)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SeallPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = SeallPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Mini Cards: Paid vs Unpaid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Paid Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SeallPaidGreenContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = SeallPaidGreen, modifier = Modifier.size(16.dp))
                            Text("Paid", style = MaterialTheme.typography.labelMedium, color = SeallPaidGreen, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = String.format(Locale.US, "₱%.2f", archive.paidTotal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SeallPaidGreen
                        )
                    }
                }

                // Unpaid Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SeallUnpaidAmberContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = SeallUnpaidAmber, modifier = Modifier.size(16.dp))
                            Text("Unpaid", style = MaterialTheme.typography.labelMedium, color = SeallUnpaidAmber, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = String.format(Locale.US, "₱%.2f", archive.unpaidTotal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SeallUnpaidAmber
                        )
                    }
                }
            }
        }

        // Orders Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = SeallPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Day's Orders (${archive.orders.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Order List
        items(archive.orders, key = { it.id }) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "₱%.2f", order.price),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        // Status Badge (clickable to toggle)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (order.isPaid) SeallPaidGreenContainer else SeallUnpaidAmberContainer)
                                .clickable { onTogglePaid(order) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (order.isPaid) "PAID" else "UNPAID",
                                color = if (order.isPaid) SeallPaidGreen else SeallUnpaidAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
