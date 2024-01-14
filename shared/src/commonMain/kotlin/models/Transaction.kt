package models

import com.technology626.budgyt.BudgetTransaction
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class Transaction(
    @Serializable(with = JavaUUIDSerializer::class)
    val id: UUID,
    @Serializable(with = BigDecimalSerializer::class)
    val transactionAmount: BigDecimal,
    val note: String,
    val transactionDate: LocalDate,
    @Serializable(with = JavaUUIDSerializer::class)
    val bucketId: UUID
)

fun BudgetTransaction.toApplicationDataModel(): Transaction {
    return Transaction(
        id = id,
        transactionAmount = transaction_amount,
        note = transaction_note,
        transactionDate = transaction_date,
        bucketId = bucket_id
    )
}
