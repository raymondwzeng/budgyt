package models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Container (
    val containerType: BucketType,
    val buckets: Map<
            @Serializable(with = JavaUUIDSerializer::class)
            UUID, Bucket>
)