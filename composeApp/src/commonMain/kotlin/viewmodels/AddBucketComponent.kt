package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import models.Bucket
import models.BucketType
import models.Container
import java.util.UUID

interface AddBucketComponent {
    fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: Float)
}

class DefaultAddBucketComponent(
    componentContext: ComponentContext,
    private val containerState: Value<List<Container>>,
    val onAddBucket: (newContainerList: List<Container>) -> Unit
) : AddBucketComponent, ComponentContext by componentContext {
    override fun addBucket(bucketName: String, bucketType: BucketType, bucketEstimate: Float) {
        val newContainerList = containerState.value.toMutableList()
        val containerIndex =
            newContainerList.indexOfFirst { container -> container.containerType == bucketType }
        val currentContainer = newContainerList[containerIndex]
        val newBucket =
            Bucket(id = UUID.randomUUID(), bucketName = bucketName, transactions = emptyList(), estimatedAmount = bucketEstimate, bucketType = bucketType)
        newContainerList[containerIndex] =
            currentContainer.copy(buckets = currentContainer.buckets + mapOf(newBucket.id to newBucket))
        onAddBucket(newContainerList)
    }
}