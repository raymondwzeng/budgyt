package views

import GLOBAL_FORMATTER
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.DeletionConfirmationDialog
import kotlinx.coroutines.launch
import viewmodels.TransactionDetailsComponent

const val TRANSACTION_DELETION_DIALOG = "Are you sure that you want to delete this transaction?"

@Composable
fun TransactionDetailView(component: TransactionDetailsComponent) {
    val deletionConfirmationState = remember { mutableStateOf(false) }
    val transaction = component.transactionModel.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    if(deletionConfirmationState.value) {
        DeletionConfirmationDialog(text = TRANSACTION_DELETION_DIALOG, onConfirm = {
            coroutineScope.launch {
                component.deleteTransaction(transaction = transaction.value)
            }
            deletionConfirmationState.value = false
        }, onDismissRequest = { deletionConfirmationState.value = false })
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        Text(text = "Transaction Details", fontWeight = FontWeight.Bold, fontSize = 32.sp, modifier = Modifier.padding(vertical = 8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Date of transaction:", fontWeight = FontWeight.Bold)
            Text(text = transaction.value.transactionDate.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Transaction Amount:", fontWeight = FontWeight.Bold)
            Text(text = GLOBAL_FORMATTER.format(transaction.value.transactionAmount))
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Transaction Note:", fontWeight = FontWeight.Bold)
            Text(text = transaction.value.note)
        }
        Button(onClick = {
            component.navigateToEditTransactionDetails(transaction.value)
        }) {
            Text(text = "Update Transaction")
        }
        Button(onClick = {
            deletionConfirmationState.value = !deletionConfirmationState.value
        }) {
            Text(text = "Delete Transaction")
        }
    }
}