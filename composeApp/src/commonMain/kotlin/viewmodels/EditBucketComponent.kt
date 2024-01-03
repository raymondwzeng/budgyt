package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import models.Bucket
import models.BucketType
import java.util.UUID

interface EditBucketComponent {
    fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: Float)
}

class DefaultEditBucketComponent(
    componentContext: ComponentContext,
    val bucket: Bucket?,
    private val database: budgyt,
    val onAddBucket: () -> Unit
) : EditBucketComponent, ComponentContext by componentContext {
    override fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: Float) {
        database.bucketQueries.addBucket(
            id = UUID.randomUUID(),
            bucket_name = bucketName,
            bucket_type = bucketType,
            bucket_estimate = bucketEstimate.toDouble()
        )
        onAddBucket()
    }
}