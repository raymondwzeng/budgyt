package views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import viewmodels.EditTransactionComponent
import viewmodels.TransactionDetailsComponent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionDetailView(component: TransactionDetailsComponent) {
    val transaction = component.transactionModel.subscribeAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
        Text(text = "Transaction Details", fontWeight = FontWeight.Bold, fontSize = 32.sp, modifier = Modifier.padding(vertical = 8.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Date of transaction:", fontWeight = FontWeight.Bold)
            Text(text = transaction.value.transactionDate.toString())
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Transaction Amount:", fontWeight = FontWeight.Bold)
            Text(text = formatter.format(transaction.value.transactionAmount))
        }
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Transaction Note:", fontWeight = FontWeight.Bold)
            Text(text = transaction.value.note)
        }
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