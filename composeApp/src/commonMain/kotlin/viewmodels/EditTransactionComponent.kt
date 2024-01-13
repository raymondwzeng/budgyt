package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import models.Bucket
import models.Transaction
import models.toApplicationDataModel
import java.math.BigDecimal
import java.util.UUID

enum class TransactionEditType {
    CREATE,
    UPDATE,
    DELETE
}

interface EditTransactionComponent {
    val currentTransaction: Transaction?
    val listBuckets: List<Bucket>
    suspend fun createTransaction(
        bucketId: UUID,
        transactionAmount: BigDecimal,
        transactionNote: String,
        transactionDate: LocalDate
    )

    suspend fun updateTransaction(updatedTransaction: Transaction)

    suspend fun deleteTransaction(transactionId: UUID)

}

class DefaultEditTransactionComponent(
    componentContext: ComponentContext,
    override val currentTransaction: Transaction?,
    val dispatcher: CoroutineDispatcher,
    private val database: budgyt,
    private val onTransactionUpdated: suspend (editType: TransactionEditType, transaction: Transaction) -> Unit,
) : EditTransactionComponent,
    ComponentContext by componentContext {

    override val listBuckets: List<Bucket>
        get() = database.bucketQueries.getBuckets().executeAsList()
            .map { bucket -> bucket.toApplicationDataModel() }

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
        withContext(dispatcher) {
            database.transactionQueries.addTransaction(
                id = transactionId,
                bucket_id = bucketId,
                transaction_date = transactionDate,
                transaction_note = transactionNote,
                transaction_amount = transactionAmount
            )
        }
        onTransactionUpdated(TransactionEditType.CREATE, newTransaction)
    }

    override suspend fun updateTransaction(
        updatedTransaction: Transaction
    ) {
        withContext(dispatcher) {
            database.transactionQueries.updateTransaction(
                transaction_amount = updatedTransaction.transactionAmount,
                transaction_note = updatedTransaction.note,
                transaction_date = updatedTransaction.transactionDate,
                bucket_id = updatedTransaction.bucketId,
                id = updatedTransaction.id
            )
        }
        onTransactionUpdated(TransactionEditType.UPDATE, updatedTransaction)
    }

    override suspend fun deleteTransaction(transactionId: UUID) {
        lateinit var transaction: Transaction
        withContext(dispatcher) {
            transaction =
                database.transactionQueries.getTransactionById(transactionId).executeAsOne()
                    .toApplicationDataModel()
            database.transactionQueries.deleteTransaction(id = transaction.id) //TODO: Check for race condition
        }
        onTransactionUpdated(
            TransactionEditType.DELETE,
            transaction
        ) //TODO: Consider refactoring - this is a big dangerous as the transaction technically shouldn't exist anymore
    }
}