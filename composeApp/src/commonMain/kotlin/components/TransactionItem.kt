package components

import GLOBAL_FORMATTER
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Transaction
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionItem(modifier: Modifier = Modifier, transaction: Transaction, onClick: () -> Unit) {
    Card(modifier = modifier, elevation = 8.dp, onClick = onClick) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(text = transaction.transactionDate.toString())
                Text(text = transaction.note)
            }
            Column {
                Text(text = GLOBAL_FORMATTER.format(transaction.transactionAmount), fontSize = 18.sp)
            }
        }
    }
}