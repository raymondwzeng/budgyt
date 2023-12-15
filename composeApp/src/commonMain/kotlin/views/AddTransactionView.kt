package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import models.Bucket
import viewmodels.ListComponent
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionView(component: ListComponent) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    val transactionAmount = remember { mutableStateOf(0f) }
    val transactionNote = remember { mutableStateOf("") }
    val transactionDate =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis(), initialDisplayMode = DisplayMode.Input)
    val currentBucket = remember { mutableStateOf<Bucket?>(null) }
    println(component.model.value)
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
                }
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                component.model.value.forEach { container -> //TODO: O(n^2) operation. Can be better?
                    container.buckets.forEach { bucket ->
                        BucketDropdown(bucket, onClick = {
                            currentBucket.value = bucket
                            expanded.value = false
                        })
                    }
                }
            }
        }
        Text(text = "Transaction Amount", fontSize = 24.sp)
        TextField(
            value = formatter.format(transactionAmount.value),
            onValueChange = { newAmount -> transactionAmount.value = newAmount.substring(1).toFloat() },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Text(text = "Transaction Note", fontSize = 24.sp)
        TextField(
            value = transactionNote.value,
            onValueChange = { newNote: String -> transactionNote.value = newNote }
        )
        Text(text = "Transaction Date", fontSize = 24.sp)
        DatePicker(state = transactionDate)

    }
}

@Composable
fun BucketDropdown(bucket: Bucket, onClick: () -> Unit) {
    DropdownMenuItem(onClick = onClick) {
        Text(text = bucket.bucketName, fontSize = 16.sp)
    }
}