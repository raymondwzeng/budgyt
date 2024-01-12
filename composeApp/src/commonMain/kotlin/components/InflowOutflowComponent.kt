package components

import GLOBAL_FORMATTER
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.Container
import returnMonetaryValueColor

@Composable
fun InflowOutflowComponent(
    modifier: Modifier = Modifier,
    inflowContainers: List<Container> = emptyList(),
    outflowContainers: List<Container> = emptyList(),
    fundContainers: List<Container> = emptyList()
) {
    val totalInflow =
        inflowContainers.sumOf { container -> container.buckets.values.sumOf { bucket -> bucket.transactions.sumOf { transaction -> transaction.transactionAmount } } }
    val totalOutflow =
        outflowContainers.sumOf { container -> container.buckets.values.sumOf { bucket -> bucket.transactions.sumOf { transaction -> transaction.transactionAmount } } }
    val totalFunding =
        fundContainers.sumOf { container -> container.buckets.values.sumOf { bucket -> bucket.transactions.sumOf { transaction -> transaction.transactionAmount } } }
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total Inflow:")
            Text(text = GLOBAL_FORMATTER.format(totalInflow))
        }
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total Outflow:")
            Text(text = GLOBAL_FORMATTER.format(totalOutflow))
        }
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total Value of Funds:")
            Text(text = GLOBAL_FORMATTER.format(totalFunding))
        }
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Net Income Flow:", fontSize = 18.sp)
            Text(
                text = GLOBAL_FORMATTER.format(totalInflow - totalOutflow),
                fontSize = 18.sp,
                color = returnMonetaryValueColor(totalInflow - totalOutflow)
            )
        }
    }
}