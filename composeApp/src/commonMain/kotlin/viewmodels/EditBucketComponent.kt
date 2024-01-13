package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Bucket
import models.BucketType
import java.math.BigDecimal
import java.util.UUID

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
    val dispatcher: CoroutineDispatcher,
    private val database: budgyt,
    val onAddBucket: (bucketId: UUID) -> Unit
) : EditBucketComponent, ComponentContext by componentContext {
    override suspend fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: BigDecimal) {
        val newBucketId = UUID.randomUUID()
        withContext(dispatcher) {
            database.bucketQueries.addBucket(
                id = newBucketId,
                bucket_name = bucketName,
                bucket_type = bucketType,
                bucket_estimate = bucketEstimate
            )
        }
        onAddBucket(newBucketId)
    }

    override suspend fun editBucket(
        bucketId: UUID,
        bucketName: String,
        bucketType: BucketType,
        bucketEstimate: BigDecimal
    ) {
        withContext(dispatcher) {
            database.bucketQueries.editBucket(
                bucket_name = bucketName,
                bucket_type = bucketType,
                bucket_estimate = bucketEstimate,
                id = bucketId
            )
        }
        onAddBucket(bucketId)
    }
}