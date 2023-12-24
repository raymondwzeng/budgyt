package models

import app.cash.sqldelight.Transacter
import com.technology626.budgyt.BudgetTransaction
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Transaction(
    @Serializable(with = JavaUUIDSerializer::class)
    val id: UUID,
    val transactionAmount: Float,
    val note: String,
    val transactionDate: LocalDate
)

fun BudgetTransaction.toApplicationDataModel(): Transaction {
    return Transaction(
        id = id,
        transactionAmount = transaction_amount.toFloat(),
        note = transaction_note,
        transactionDate = transaction_date
    )
}
