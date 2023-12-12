package components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID


val EXAMPLE_BUDGET = listOf(
    Transaction(
        id = UUID.randomUUID(),
        note = "Test note",
        transactionAmount = 12.2f,
        transactionDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
)

val EXAMPLE_BUDGET_2 = listOf(
    Transaction(
        id = UUID.randomUUID(),
        note = "Test note 2",
        transactionAmount = 42.3f,
        transactionDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
)


val EXAMPLE_BUCKET = Bucket(
    id = UUID.randomUUID(),
    bucketName = "Example Bucket",
    transactions = EXAMPLE_BUDGET
)

val EXAMPLE_BUCKET_2 = Bucket(
    id = UUID.randomUUID(),
    bucketName = "Example Bucket 2",
    transactions = EXAMPLE_BUDGET_2
)

val EXAMPLE_CONTAINER = Container(
    containerType = BucketType.OUTFLOW,
    buckets = listOf(EXAMPLE_BUCKET, EXAMPLE_BUCKET_2)
)

val EMPTY_INFLOW_CONTAINER = Container(
    containerType = BucketType.INFLOW,
    buckets = emptyList()
)

val EMPTY_FUND_CONTAINER = Container(
    containerType = BucketType.FUND,
    buckets = emptyList()
)

val EXAMPLE_CONTAINERS = listOf(EMPTY_INFLOW_CONTAINER, EXAMPLE_CONTAINER, EMPTY_FUND_CONTAINER)

@Composable
fun Bucket() { //Really, this is a bucket of buckets.
    //This component should also hold the state for the inner internal items
    LazyColumn {
        items(EXAMPLE_CONTAINERS) { container ->
            Text(
                text = when(container.containerType) {
                    BucketType.INFLOW -> "Inflow"
                    BucketType.OUTFLOW -> "Outflow"
                    BucketType.FUND -> "Fund"
                },
                fontSize = 32.sp,
                modifier = Modifier.padding(8.dp)
            )
            if(container.buckets.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(0.8f)
                    .padding(16.dp).height(IntrinsicSize.Min), elevation = 4.dp) {
                    Text("No buckets for this category yet!", textAlign = TextAlign.Center, modifier = Modifier.padding(4.dp))
                }
            } else {
                container.buckets.forEach { bucket ->
                    BudgetCard(
                        name = bucket.bucketName,
                        estimatedAmount = 100f,
                        actualAmount = bucket.transactions.sumOf { it.transactionAmount.toDouble() },
                        onClick = fun() {
                            println(bucket)
                        }
                    )
                }
            }
        }
    }
}