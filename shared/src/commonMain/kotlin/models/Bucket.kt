package models

import kotlinx.serialization.Serializable
import java.util.UUID

enum class BucketType {
    INFLOW, OUTFLOW, FUND
}

@Serializable
data class Bucket(
    @Serializable(with = JavaUUIDSerializer::class)
    val id: UUID,
    val bucketName: String,
    val bucketType: BucketType
)
