package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Bucket
import models.Transaction
import networking.repository.BucketRepositoryHttp
import repository.BucketRepository
import repository.TransactionRepository

interface DetailsComponent {
    val bucketModel: MutableValue<Bucket>

    fun navigateToTransactionDetail(transaction: Transaction)

    fun navigateToEditBucket()
    suspend fun removeBucket()
}

class DefaultDetailsComponent(
    componentContext: ComponentContext,
    item: MutableValue<Bucket>,
    val bucketRepository: BucketRepository,
    private val bucketRepositoryHttp: BucketRepositoryHttp,
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
        bucketRepository.deleteBucket(bucketModel.value.id)
        try {
            bucketRepositoryHttp.deleteBucket(bucketModel.value.id)
        } catch (exception: Exception) {
            //TODO: Log exception so that it's easier to catch errors
        }
        onFinished()
    }
}