package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Bucket
import models.BucketType
import networking.repository.BucketRepositoryHttp
import repository.BucketRepository
import repository.TransactionRepository
import java.math.BigDecimal
import java.util.UUID
import java.util.logging.Logger

interface EditBucketComponent {
    val bucket: Bucket?
    suspend fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: BigDecimal)

    suspend fun editBucket(
        bucketId: UUID,
        bucketName: String,
        bucketType: BucketType,
        bucketEstimate: BigDecimal
    )
}

class DefaultEditBucketComponent(
    componentContext: ComponentContext,
    override val bucket: Bucket?,
    private val bucketRepository: BucketRepository,
    private val bucketRepositoryHttp: BucketRepositoryHttp,
    val onAddBucket: (bucketId: UUID) -> Unit
) : EditBucketComponent, ComponentContext by componentContext {
    override suspend fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: BigDecimal) {
        val newBucket = Bucket(
            id = UUID.randomUUID(),
            bucketName = bucketName,
            bucketType = bucketType,
            estimatedAmount = bucketEstimate,
            transactions = emptyList()
        )
        bucketRepository.addBucket(newBucket)
        try {
            bucketRepositoryHttp.addBucket(newBucket)
        } catch (exception: Exception) {
            //TODO: Log exception so that it's easier to catch errors
        }
        onAddBucket(newBucket.id)
    }

    override suspend fun editBucket(
        bucketId: UUID,
        bucketName: String,
        bucketType: BucketType,
        bucketEstimate: BigDecimal
    ) {
        val updatedBucket = Bucket(
            id = bucketId,
            bucketName = bucketName,
            bucketType = bucketType,
            estimatedAmount = bucketEstimate,
            transactions = emptyList()
        )
        bucketRepository.editBucket(updatedBucket)
        try {
            bucketRepositoryHttp.editBucket(updatedBucket)
        } catch (exception: Exception) {
            //TODO: Log exception so that it's easier to catch errors
        }
        onAddBucket(bucketId)
    }
}