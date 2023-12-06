package models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Transaction(
    @Serializable(with = JavaUUIDSerializer::class)
    val id: UUID,
    val transactionAmount: Float,
    val note: String,
    val transactionDate: LocalDate,
    @Serializable(with = JavaUUIDSerializer::class)
    val bucketId: UUID
)
