package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import models.Bucket
import models.Transaction
import models.toApplicationDataModel
import java.util.UUID

enum class TransactionEditType {
    CREATE,
    UPDATE,
    DELETE
}

interface EditTransactionComponent {
    val currentTransaction: Transaction?
    val listBuckets: List<Bucket>
    fun createTransaction(bucket: Bucket, transaction: Transaction)

    fun updateTransaction(bucket: Bucket, oldTransaction: Transaction, newTransaction: Transaction)

    fun deleteTransaction(transactionId: UUID)

}

class DefaultEditTransactionComponent(
    componentContext: ComponentContext,
    override val currentTransaction: Transaction?,
    private val database: budgyt,
    private val onTransactionUpdated: (editType: TransactionEditType, transactionId: UUID, bucketId: UUID) -> Unit,
) : EditTransactionComponent,
    ComponentContext by componentContext {

    override val listBuckets: List<Bucket>
        get() = database.bucketQueries.getBuckets().executeAsList()
            .map { bucket -> bucket.toApplicationDataModel() }

    override fun createTransaction(bucket: Bucket, transaction: Transaction) {
        database.transactionQueries.addTransaction(
            id = transaction.id,
            bucket_id = bucket.id,
            transaction_date = transaction.transactionDate,
            transaction_note = transaction.note,
            transaction_amount = transaction.transactionAmount.toDouble()
        )
        onTransactionUpdated(TransactionEditType.CREATE, transaction.id, bucket.id)
    }

    override fun updateTransaction(
        bucket: Bucket,
        oldTransaction: Transaction,
        newTransaction: Transaction
    ) {
        database.transactionQueries.updateTransaction(
            transaction_amount = newTransaction.transactionAmount.toDouble(),
            transaction_note = newTransaction.note,
            transaction_date = newTransaction.transactionDate,
            bucket_id = bucket.id,
            id = oldTransaction.id
        )
        onTransactionUpdated(TransactionEditType.UPDATE, newTransaction.id, bucket.id)
    }

    override fun deleteTransaction(transactionId: UUID) {
        val bucketId = database.transactionQueries.getTransactionById(transactionId).executeAsOne().bucket_id
        database.transactionQueries.deleteTransaction(id = transactionId)
        onTransactionUpdated(TransactionEditType.DELETE, transactionId, bucketId)
    }
}