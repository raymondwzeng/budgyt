package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.technology626.budgyt.budgyt
import models.Bucket
import models.BucketType
import java.util.UUID
import java.math.BigDecimal

interface EditBucketComponent {
    val bucket: Bucket?
    fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: BigDecimal)

    fun editBucket(
        bucketId: UUID,
        bucketName: String,
        bucketType: BucketType,
        bucketEstimate: BigDecimal
    )
}

class DefaultEditBucketComponent(
    componentContext: ComponentContext,
    override val bucket: Bucket?,
    private val database: budgyt,
    val onAddBucket: (bucketId: UUID) -> Unit
) : EditBucketComponent, ComponentContext by componentContext {
    override fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: BigDecimal) {
        val newBucketId = UUID.randomUUID()
        database.bucketQueries.addBucket(
            id = newBucketId,
            bucket_name = bucketName,
            bucket_type = bucketType,
            bucket_estimate = bucketEstimate
        )
        onAddBucket(newBucketId)
    }

    override fun editBucket(
        bucketId: UUID,
        bucketName: String,
        bucketType: BucketType,
        bucketEstimate: BigDecimal
    ) {
        database.bucketQueries.editBucket(
            bucket_name = bucketName,
            bucket_type = bucketType,
            bucket_estimate = bucketEstimate,
            id = bucketId
        )
        onAddBucket(bucketId)
    }
}