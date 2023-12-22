package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID

interface ListComponent {
    val model: Value<List<Container>>
    fun onItemClicked(item: Bucket)
    fun onAddTransactionButtonClicked()

    fun navigateToAddBucketSelected()

    fun transactionAdded(bucket: Bucket, transaction: Transaction)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onAddTransactionSelected: () -> Unit,
    private val onAddBucketSelected: () -> Unit,
    private val onTransactionAdded: (newContainerList: List<Container>) -> Unit,
    containerState: Value<List<Container>>
): ListComponent, ComponentContext by componentContext {
    override val model = containerState
    override fun onItemClicked(item: Bucket) {
        onItemSelected(item)
    }

    override fun onAddTransactionButtonClicked() {
        onAddTransactionSelected()
    }

    override fun navigateToAddBucketSelected() {
        onAddBucketSelected()
    }

    override fun transactionAdded(bucket: Bucket, transaction: Transaction) {
        val modelListCopy = model.value.toMutableList()
        val containerIndex = modelListCopy.indexOfFirst { container -> container.buckets.containsKey(bucket.id) } //TODO: Optimize
        if(containerIndex != -1) {
            val newMap = modelListCopy[containerIndex].buckets.toMutableMap()
            val newBucket = bucket.copy(transactions = bucket.transactions + transaction)
            newMap[bucket.id] = newBucket
            val containerCopy = modelListCopy[containerIndex].copy(buckets = newMap)
            modelListCopy[containerIndex] = containerCopy
        }
        onTransactionAdded(modelListCopy)
    }
}