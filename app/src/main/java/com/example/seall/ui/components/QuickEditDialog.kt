package com.example.seall.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.seall.data.model.StockItem
import com.example.seall.ui.theme.SeallDarkContrast
import com.example.seall.ui.theme.SeallPrimary
import java.util.Locale

@Composable
fun QuickEditDialog(
    title: String,
    initialValue: String,
    label: String,
    isNumeric: Boolean = false,
    prefix: String? = null,
    stocks: List<StockItem> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialValue,
                selection = TextRange(0, initialValue.length)
            )
        )
    }
    var isError by remember { mutableStateOf(false) }
    var stockDropdownExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = { newVal ->
                            if (isNumeric) {
                                val filtered = newVal.text.filter { it.isDigit() || it == '.' }
                                if (filtered.count { it == '.' } <= 1) {
                                    textFieldValue = newVal.copy(text = filtered)
                                    isError = false
                                }
                            } else {
                                textFieldValue = newVal
                                if (newVal.text.isNotBlank()) isError = false
                            }
                        },
                        label = { Text(label) },
                        isError = isError,
                        supportingText = if (isError) {
                            {
                                Text(
                                    text = if (isNumeric) "Enter a valid amount" else "Cannot be blank",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        } else null,
                        singleLine = true,
                        leadingIcon = if (prefix != null) {
                            {
                                Text(
                                    text = prefix,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SeallPrimary,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            }
                        } else null,
                        trailingIcon = if (isNumeric && stocks.isNotEmpty()) {
                            {
                                Box(modifier = Modifier.padding(end = 4.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (stockDropdownExpanded) SeallPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { stockDropdownExpanded = !stockDropdownExpanded }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Stocks",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (stockDropdownExpanded) SeallPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Icon(
                                                imageVector = if (stockDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = "Choose stock",
                                                tint = if (stockDropdownExpanded) SeallPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = stockDropdownExpanded,
                                        onDismissRequest = { stockDropdownExpanded = false }
                                    ) {
                                        stocks.forEach { stock ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            text = stock.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        Spacer(modifier = Modifier.width(16.dp))
                                                        Text(
                                                            text = String.format(Locale.US, "₱%.2f", stock.price),
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SeallPrimary
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    val formatted = if (stock.price % 1.0 == 0.0) {
                                                        stock.price.toLong().toString()
                                                    } else {
                                                        String.format(Locale.US, "%.2f", stock.price)
                                                    }
                                                    textFieldValue = TextFieldValue(
                                                        text = formatted,
                                                        selection = TextRange(0, formatted.length)
                                                    )
                                                    isError = false
                                                    stockDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        } else null,
                        keyboardOptions = KeyboardOptions(
                            capitalization = if (isNumeric) KeyboardCapitalization.None else KeyboardCapitalization.Words,
                            keyboardType = if (isNumeric) KeyboardType.Decimal else KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val txt = textFieldValue.text.trim()
                                if (isNumeric) {
                                    val num = txt.toDoubleOrNull()
                                    if (num != null && num > 0.0) {
                                        onConfirm(txt)
                                    } else {
                                        isError = true
                                    }
                                } else {
                                    if (txt.isNotBlank()) {
                                        onConfirm(txt)
                                    } else {
                                        isError = true
                                    }
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SeallPrimary,
                            focusedLabelColor = SeallPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(
                                text = "Cancel",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                val txt = textFieldValue.text.trim()
                                if (isNumeric) {
                                    val num = txt.toDoubleOrNull()
                                    if (num != null && num > 0.0) {
                                        onConfirm(txt)
                                    } else {
                                        isError = true
                                    }
                                } else {
                                    if (txt.isNotBlank()) {
                                        onConfirm(txt)
                                    } else {
                                        isError = true
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeallPrimary,
                                contentColor = SeallDarkContrast
                            )
                        ) {
                            Text(
                                text = "Save",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
