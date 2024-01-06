package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.technology626.budgyt.budgyt
import models.Transaction
import java.util.UUID

interface TransactionDetailsComponent {
    val transactionModel: MutableValue<Transaction>
    fun navigateToEditTransactionDetails(transaction: Transaction)

    fun deleteTransaction(transactionId: UUID)
}

class DefaultTransactionDetailsComponent(
    componentContext: ComponentContext,
    private val database: budgyt,
    transactionModel: MutableValue<Transaction>,
    private val onNavigateToEditTransactionDetails: (transaction: Transaction) -> Unit,
    private val onDeleteTransaction: (bucketId: UUID) -> Unit
) :
    TransactionDetailsComponent, ComponentContext by componentContext {

    override val transactionModel: MutableValue<Transaction> = transactionModel
    override fun navigateToEditTransactionDetails(transaction: Transaction) {
        onNavigateToEditTransactionDetails(transaction)
    }

    override fun deleteTransaction(transactionId: UUID) {
        val bucketId = database.transactionQueries.getTransactionById(transactionId).executeAsOne().bucket_id
        database.transactionQueries.deleteTransaction(transactionId)
        onDeleteTransaction(bucketId)
    }

}