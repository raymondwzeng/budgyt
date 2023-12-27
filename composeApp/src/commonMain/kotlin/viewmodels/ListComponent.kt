package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.technology626.budgyt.budgyt
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID

interface ListComponent {
    fun onItemClicked(item: Bucket)
    fun onAddTransactionButtonClicked()

    fun navigateToAddBucketSelected()

    fun transactionAdded(bucket: Bucket, transaction: Transaction)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    private val database: budgyt,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onAddTransactionSelected: () -> Unit,
    private val onAddBucketSelected: () -> Unit,
    private val onTransactionAdded: () -> Unit
) : ListComponent, ComponentContext by componentContext {
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
        database.transactionQueries.addTransaction(
            id = transaction.id,
            bucket_id = bucket.id,
            transaction_date = transaction.transactionDate,
            transaction_note = transaction.note,
            transaction_amount = transaction.transactionAmount.toDouble()
        )
        onTransactionAdded()
    }
}