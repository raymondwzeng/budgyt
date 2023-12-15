package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
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

interface ListComponent {
    val model: Value<List<Container>>
    fun onItemClicked(item: Bucket)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    private val onItemSelected: (item: Bucket) -> Unit
): ListComponent, ComponentContext by componentContext {
    override val model = MutableValue(EXAMPLE_CONTAINERS)

    override fun onItemClicked(item: Bucket) {
        onItemSelected(item)
    }
}