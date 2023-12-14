package components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Transaction
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TransactionItem(modifier: Modifier = Modifier, transaction: Transaction) {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    Card(modifier = modifier, elevation = 8.dp) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = transaction.transactionDate.toString())
                Text(text = transaction.note)
            }
            Column {
                Text(text = formatter.format(transaction.transactionAmount), fontSize = 18.sp)
            }
        }
    }
}