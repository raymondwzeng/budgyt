package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.technology626.budgyt.budgyt
import models.Bucket
import models.Transaction
import models.toApplicationDataModel
import java.util.UUID

interface TransactionComponent {
    val currentTransaction: Transaction?
    val listBuckets: List<Bucket>
    fun createTransaction(bucket: Bucket, transaction: Transaction)

    fun updateTransaction(bucket: Bucket, oldTransaction: Transaction, newTransaction: Transaction)

    fun deleteTransaction(transactionId: UUID)
}

class DefaultTransactionComponent(
    componentContext: ComponentContext,
    override val currentTransaction: Transaction?,
    private val database: budgyt,
    private val onTransactionAdded: () -> Unit
    ) : TransactionComponent,
    ComponentContext by componentContext {

    override val listBuckets: List<Bucket>
        get() = database.bucketQueries.getBuckets().executeAsList().map { bucket -> bucket.toApplicationDataModel() }
    override fun createTransaction(bucket: Bucket, transaction: Transaction) {
        database.transactionQueries.addTransaction(
            id = transaction.id,
            bucket_id = bucket.id,
            transaction_date = transaction.transactionDate,
            transaction_note = transaction.note,
            transaction_amount = transaction.transactionAmount.toDouble()
        )
        onTransactionAdded()
    }

    override fun updateTransaction(
        bucket: Bucket,
        oldTransaction: Transaction,
        newTransaction: Transaction
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteTransaction(transactionId: UUID) {
        TODO("Not yet implemented")
    }
}