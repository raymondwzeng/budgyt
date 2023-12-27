package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.technology626.budgyt.budgyt
import models.Bucket
import models.BucketType
import models.Container
import java.util.UUID

interface AddBucketComponent {
    fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: Float)
}

class DefaultAddBucketComponent(
    componentContext: ComponentContext,
    private val database: budgyt,
    val onAddBucket: () -> Unit
) : AddBucketComponent, ComponentContext by componentContext {
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