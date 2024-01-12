package components

import GLOBAL_FORMATTER
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Transaction
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransactionItem(modifier: Modifier = Modifier, transaction: Transaction, onClick: () -> Unit) {
    Card(modifier = modifier, elevation = 4.dp, onClick = onClick) {
        Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            Text(text = transaction.transactionDate.toString(), fontSize = 12.sp, fontWeight = FontWeight.Light)
            Text(text = GLOBAL_FORMATTER.format(transaction.transactionAmount), fontSize = 18.sp, textAlign = TextAlign.End)
            Text(text = transaction.note)
        }
    }
}