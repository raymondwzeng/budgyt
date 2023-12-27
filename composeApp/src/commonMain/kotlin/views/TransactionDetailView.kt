package views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import viewmodels.EditTransactionComponent
import viewmodels.TransactionDetailsComponent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionDetailView(component: TransactionDetailsComponent) {
    val transaction = component.transactionModel.subscribeAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    Column {
        Text(text = transaction.value.id.toString())
        Text(text = transaction.value.transactionDate.toString())
        Text(text = formatter.format(transaction.value.transactionAmount))
        Text(text = transaction.value.note)
        Button(onClick = {
            component.deleteTransaction(transactionId = transaction.value.id)
        }) {
            Text(text = "Delete Transaction")
        }
        Button(onClick = {
            component.navigateToEditTransactionDetails(transaction.value)
        }) {
            Text(text = "Update Transaction")
        }
    }
}