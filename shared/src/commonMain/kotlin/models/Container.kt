package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Container(
    val containerType: BucketType,
    val buckets: Map<
            @Serializable(with = JavaUUIDSerializer::class)
            UUID, Bucket>
)

fun List<Bucket>.toContainerList(): List<Container> {
    if (this.isEmpty()) {
        throw Exception("Unable to construct a container using this function from an empty list of buckets!")
    }
    return this.groupBy { bucket ->
        bucket.bucketType
    }.map { entry ->
        Container(
            containerType = entry.key,
            buckets = entry.value.associateBy { bucket -> bucket.id }
        )
    }
}