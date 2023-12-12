package models

import kotlinx.serialization.Serializable

@Serializable
data class Container (
    val containerType: BucketType,
    val buckets: List<Bucket>
)