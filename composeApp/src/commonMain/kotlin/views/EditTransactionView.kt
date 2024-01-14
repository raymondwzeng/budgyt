package views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.TransactionInputComponent
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import models.Bucket
import viewmodels.EditTransactionComponent
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionView(component: EditTransactionComponent) {
    val transactionAmount =
        remember {
            mutableStateOf(
                component.currentTransaction?.transactionAmount ?: BigDecimal(0.0)
            )
        }
    val transactionNote = remember { mutableStateOf(component.currentTransaction?.note ?: "") }
    val transactionDate =
        rememberDatePickerState(
            initialSelectedDateMillis = if (component.currentTransaction != null) {
                component.currentTransaction?.transactionDate?.atStartOfDayIn(TimeZone.currentSystemDefault())
                    ?.toEpochMilliseconds()
            } else {
                System.currentTimeMillis()
            }, initialDisplayMode = DisplayMode.Input
        )
    val bucketList = remember { mutableStateOf(listOf<Bucket>()) }
    val currentBucket =
        remember { mutableStateOf(bucketList.value.find { bucket -> bucket.id == component.currentTransaction?.bucketId }) }
    val bucketDropdownExpanded = remember { mutableStateOf(false) }
    val showDateSelectionDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch { //TODO: Code smell. This should only be ran once but runs every recomposition
        bucketList.value = component.getBuckets().sortedBy { bucket -> bucket.bucketType }
    }
    LaunchedEffect(bucketList.value) {
        currentBucket.value =
            bucketList.value.find { bucket -> bucket.id == component.currentTransaction?.bucketId }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //TODO: Component-ize
        Text(text = "Bucket", fontSize = 24.sp)
        Surface {
            TextField(
                value = currentBucket.value?.bucketName ?: "Select Bucket",
                onValueChange = {},
                trailingIcon = {
                    IconButton(onClick = {
                        bucketDropdownExpanded.value = !bucketDropdownExpanded.value
                    }) {
                        if (bucketDropdownExpanded.value) {
                            Icon(Icons.Filled.ExpandLess, contentDescription = "Expand less")
                        } else {
                            Icon(Icons.Filled.ExpandMore, contentDescription = "Expand more")
                        }
                    }
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = bucketDropdownExpanded.value,
                onDismissRequest = { bucketDropdownExpanded.value = false }
            ) {
                bucketList.value.forEach { bucket ->
                    BucketDropdown(bucket, onClick = {
                        currentBucket.value = bucket
                        bucketDropdownExpanded.value = false
                    })
                }
            }
        }
        Text(text = "Transaction Amount", fontSize = 24.sp)
        TransactionInputComponent(value = transactionAmount.value, onInputChange = { newValue ->
            transactionAmount.value = newValue
        })
        Text(text = "Transaction Note", fontSize = 24.sp)
        TextField(
            value = transactionNote.value,
            onValueChange = { newNote: String -> transactionNote.value = newNote }
        )
        Text(text = "Transaction Date", fontSize = 24.sp)
        TextField(
            value = Instant.fromEpochMilliseconds(transactionDate.selectedDateMillis ?: 0)
                .toLocalDateTime(TimeZone.UTC).date.toString(),
            readOnly = true,
            onValueChange = {},
            trailingIcon = {
                IconButton(onClick = {
                    showDateSelectionDialog.value = !showDateSelectionDialog.value
                }) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = "Select date")
                }
            }
        )
        if (showDateSelectionDialog.value) {
            DatePickerDialog(
                onDismissRequest = { showDateSelectionDialog.value = false },
                confirmButton = {
                    Text(text = "OK", modifier = Modifier.padding(16.dp).clickable(onClick = {
                        showDateSelectionDialog.value = false
                    }))
                },
                dismissButton = { //TODO: Remember old date chosen and revert onDismiss
                    Text(text = "Cancel", modifier = Modifier.padding(16.dp).clickable(onClick = {
                        showDateSelectionDialog.value = false
                    }))
                }) {
                DatePicker(state = transactionDate)
            }
        }
        val transaction = component.currentTransaction
        if (transaction != null) {
            Button(onClick = {
                val selectedBucket = currentBucket.value
                if (selectedBucket != null) {
                    coroutineScope.launch {
                        component.updateTransaction(
                            transaction.copy(
                                transactionAmount = transactionAmount.value,
                                transactionDate = Instant.fromEpochMilliseconds(
                                    transactionDate.selectedDateMillis ?: 0
                                )
                                    .toLocalDateTime(
                                        TimeZone.UTC
                                    ).date,
                                note = transactionNote.value,
                                bucketId = selectedBucket.id
                            )
                        )
                    }
                }
            }) {
                Text(text = "Update Transaction")
            }
        } else {
            Button(onClick = {
                val onClickValue = currentBucket.value
                if (onClickValue != null) {
                    coroutineScope.launch {
                        component.createTransaction(
                            bucketId = onClickValue.id,
                            transactionAmount = transactionAmount.value,
                            transactionDate = Instant.fromEpochMilliseconds(
                                transactionDate.selectedDateMillis ?: 0
                            ).toLocalDateTime(
                                TimeZone.UTC
                            ).date,
                            transactionNote = transactionNote.value
                        )
                    }
                }
            }) {
                Text(text = "Add New Transaction")
            }
        }
    }
}

@Composable
fun BucketDropdown(bucket: Bucket, onClick: () -> Unit) {
    DropdownMenuItem(onClick = onClick) {
        Text(text = bucket.bucketName, fontSize = 16.sp)
    }
}