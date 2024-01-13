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

    suspend fun updateTransaction(bucketId: UUID, oldTransaction: Transaction, newTransaction: Transaction)

    suspend fun deleteTransaction(transactionId: UUID)

}

class DefaultEditTransactionComponent(
    componentContext: ComponentContext,
    override val currentTransaction: Transaction?,
    val dispatcher: CoroutineDispatcher,
    private val database: budgyt,
    private val onTransactionUpdated: suspend (editType: TransactionEditType, transactionId: UUID, bucketId: UUID) -> Unit,
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
        withContext(dispatcher) {
            database.transactionQueries.addTransaction(
                id = transactionId,
                bucket_id = bucketId,
                transaction_date = transactionDate,
                transaction_note = transactionNote,
                transaction_amount = transactionAmount
            )
        }
        onTransactionUpdated(TransactionEditType.CREATE, transactionId, bucketId)
    }

    override suspend fun updateTransaction(
        bucketId: UUID,
        oldTransaction: Transaction,
        newTransaction: Transaction
    ) {
        if (oldTransaction.id != newTransaction.id) {
            throw Exception("ERROR: Old transaction id is not the same as new transaction id! This is unexpected behavior.")
        }
        withContext(dispatcher) {
            database.transactionQueries.updateTransaction(
                transaction_amount = newTransaction.transactionAmount,
                transaction_note = newTransaction.note,
                transaction_date = newTransaction.transactionDate,
                bucket_id = bucketId,
                id = oldTransaction.id
            )
        }
        onTransactionUpdated(TransactionEditType.UPDATE, newTransaction.id, bucketId)
    }

    override suspend fun deleteTransaction(transactionId: UUID) {
        val bucketId =
            database.transactionQueries.getTransactionById(transactionId).executeAsOne().bucket_id
        withContext(dispatcher) {
            database.transactionQueries.deleteTransaction(id = transactionId)
        }
        onTransactionUpdated(TransactionEditType.DELETE, transactionId, bucketId)
    }
}