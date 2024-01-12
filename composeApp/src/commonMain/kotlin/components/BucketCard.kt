package components

import GLOBAL_FORMATTER
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import java.math.BigDecimal

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BucketCard(
    name: String,
    actualAmount: BigDecimal,
    estimatedAmount: BigDecimal,
    onClick: () -> Unit = {}
) {
    MaterialTheme {
        Card(modifier = Modifier.fillMaxWidth(0.8f)
            .padding(16.dp).height(IntrinsicSize.Min), elevation = 4.dp, onClick = onClick) {
            Column(modifier = Modifier.padding(4.dp)) {
                Text(text=name, fontSize = 24.sp)
                Text(textAlign = TextAlign.Left, text=GLOBAL_FORMATTER.format(actualAmount), fontSize = 24.sp)
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)){
                    Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right, text = "Estimated: ${GLOBAL_FORMATTER.format(estimatedAmount)}")
                }
            }
        }
    }
}