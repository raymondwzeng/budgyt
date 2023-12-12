package components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
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
    bucketType = BucketType.OUTFLOW,
    transactions = EXAMPLE_BUDGET
)

val EXAMPLE_BUCKET_2 = Bucket(
    id = UUID.randomUUID(),
    bucketName = "Example Bucket 2",
    bucketType = BucketType.INFLOW,
    transactions = EXAMPLE_BUDGET_2
)

val EXAMPLE_BUCKET_LIST = listOf(EXAMPLE_BUCKET, EXAMPLE_BUCKET_2)

@Composable
fun Bucket() {
    //TODO: Parameters for text input/bucket type and everything else.
    //This component should also hold the state for the inner internal items
    LazyColumn { //TODO: Make sure that each category is only serviced once
        items(EXAMPLE_BUCKET_LIST) { bucket ->
            Text(
                text = when(bucket.bucketType) {
                    BucketType.INFLOW -> "Inflow"
                    BucketType.OUTFLOW -> "Outflow"
                    BucketType.FUND -> "Fund"
                },
                fontSize = 32.sp,
                modifier = Modifier.padding(8.dp)
            )
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