package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Bucket
import models.Transaction

interface DetailsComponent {
    val bucketModel: MutableValue<Bucket>

    fun navigateToTransactionDetail(transaction: Transaction)

    fun navigateToEditBucket()
    suspend fun removeBucket()
}

class DefaultDetailsComponent(
    componentContext: ComponentContext,
    item: MutableValue<Bucket>,
    val dispatcher: CoroutineDispatcher,
    val database: budgyt,
    private val onNavigateToTransactionDetails: (transaction: Transaction) -> Unit,
    private val onNavigateToEditBucket: (bucket: Bucket) -> Unit,
    val onFinished: () -> Unit
): DetailsComponent, ComponentContext by componentContext {
    override val bucketModel: MutableValue<Bucket> = item
    override fun navigateToTransactionDetail(transaction: Transaction) {
        onNavigateToTransactionDetails(transaction)
    }

    override fun navigateToEditBucket() {
        onNavigateToEditBucket(bucketModel.value)
    }

    override suspend fun removeBucket() {
        withContext(dispatcher) {
            database.bucketQueries.deleteBucket(bucketModel.value.id)
        }
        onFinished()
    }
}