package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import models.Bucket
import models.Transaction
import viewmodels.EditTransactionComponent
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionView(component: EditTransactionComponent) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    val transactionAmount =
        remember { mutableStateOf(component.currentTransaction?.transactionAmount ?: 0f) }
    val transactionNote = remember { mutableStateOf(component.currentTransaction?.note ?: "") }
    val transactionDate =
        rememberDatePickerState(
            initialSelectedDateMillis = if (component.currentTransaction != null) {
                component.currentTransaction?.transactionDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
            } else {
                System.currentTimeMillis()
            }, initialDisplayMode = DisplayMode.Input
        )
    val currentBucket = remember { mutableStateOf<Bucket?>(null) }
    val expanded = remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //TODO: Component-ize
        Text(text = "Bucket", fontSize = 24.sp)
        Surface {
            TextField(
                value = currentBucket.value?.bucketName ?: "Select Bucket",
                onValueChange = {},
                trailingIcon = {
                    Button(onClick = {
                        expanded.value = !expanded.value
                    }) {
                        Text(">")
                    }
                },
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                component.listBuckets.forEach { bucket ->
                    BucketDropdown(bucket, onClick = {
                        currentBucket.value = bucket
                        expanded.value = false
                    })
                }
            }
        }
        Text(text = "Transaction Amount", fontSize = 24.sp)
        TextField(
            value = formatter.format(transactionAmount.value),
            onValueChange = { newAmount ->
                transactionAmount.value = newAmount.substring(1).toFloat()
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Text(text = "Transaction Note", fontSize = 24.sp)
        TextField(
            value = transactionNote.value,
            onValueChange = { newNote: String -> transactionNote.value = newNote }
        )
        Text(text = "Transaction Date", fontSize = 24.sp)
        DatePicker(state = transactionDate)
        val transaction = component.currentTransaction
        if (transaction != null) {
            Button(onClick = {
                transaction.id.let { uuid ->
                    component.deleteTransaction(uuid)
                }
            }) {
                Text(text = "Delete Transaction")
            }
            Button(onClick = {
                val selectedBucket = currentBucket.value
                if (selectedBucket != null) {
                    component.updateTransaction(
                        selectedBucket, transaction, transaction.copy(
                            transactionAmount = transactionAmount.value,
                            transactionDate = Instant.fromEpochMilliseconds(transactionDate.selectedDateMillis ?: 0)
                                .toLocalDateTime(
                                    TimeZone.UTC
                                ).date,
                            note = transactionNote.value
                        )
                    )
                }
            }) {
                Text(text = "Update Transaction")
            }
        } else {
            Button(onClick = {
                val onClickValue = currentBucket.value
                if (onClickValue != null) {
                    component.createTransaction(
                        bucket = onClickValue,
                        transaction = Transaction(
                            id = UUID.randomUUID(),
                            transactionAmount = transactionAmount.value,
                            note = transactionNote.value,
                            transactionDate = Instant.fromEpochMilliseconds(
                                transactionDate.selectedDateMillis ?: 0
                            ).toLocalDateTime(
                                TimeZone.UTC
                            ).date
                        )
                    )
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