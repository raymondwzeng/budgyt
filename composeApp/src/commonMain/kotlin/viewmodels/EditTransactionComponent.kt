package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import models.Bucket
import models.Transaction
import models.toApplicationDataModel
import repository.BucketRepository
import repository.TransactionRepository
import java.math.BigDecimal
import java.util.UUID

enum class TransactionEditType {
    CREATE,
    UPDATE,
    DELETE
}

interface EditTransactionComponent {
    val currentTransaction: Transaction?
    suspend fun createTransaction(
        bucketId: UUID,
        transactionAmount: BigDecimal,
        transactionNote: String,
        transactionDate: LocalDate
    )

    suspend fun getBuckets(): List<Bucket>

    suspend fun updateTransaction(updatedTransaction: Transaction)

    suspend fun deleteTransaction(transactionId: UUID)

}

class DefaultEditTransactionComponent(
    componentContext: ComponentContext,
    override val currentTransaction: Transaction?,
    private val transactionRepository: TransactionRepository,
    private val bucketRepository: BucketRepository,
    private val onTransactionUpdated: suspend (editType: TransactionEditType, transaction: Transaction) -> Unit,
) : EditTransactionComponent,
    ComponentContext by componentContext {
    override suspend fun getBuckets(): List<Bucket> {
        return bucketRepository.getBuckets().getOrDefault(emptyList())
    }

    override suspend fun createTransaction(
        bucketId: UUID,
        transactionAmount: BigDecimal,
        transactionNote: String,
        transactionDate: LocalDate
    ) {
        val transactionId = UUID.randomUUID()
        val newTransaction = Transaction(
            id = transactionId,
            bucketId = bucketId,
            note = transactionNote,
            transactionAmount = transactionAmount,
            transactionDate = transactionDate
        )
        transactionRepository.addTransaction(newTransaction)
        onTransactionUpdated(TransactionEditType.CREATE, newTransaction)
    }

    override suspend fun updateTransaction(
        updatedTransaction: Transaction
    ) {
        transactionRepository.updateTransaction(updatedTransaction)
        onTransactionUpdated(TransactionEditType.UPDATE, updatedTransaction)
    }

    override suspend fun deleteTransaction(transactionId: UUID) {
        lateinit var transaction: Transaction
        transactionRepository.deleteTransaction(transactionId)
        onTransactionUpdated(
            TransactionEditType.DELETE,
            transaction
        ) //TODO: Consider refactoring - this is a big dangerous as the transaction technically shouldn't exist anymore
    }
}