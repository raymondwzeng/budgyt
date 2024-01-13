package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Transaction
import java.util.UUID

interface TransactionDetailsComponent {
    val transactionModel: MutableValue<Transaction>
    fun navigateToEditTransactionDetails(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}

class DefaultTransactionDetailsComponent(
    componentContext: ComponentContext,
    private val database: budgyt,
    val dispatcher: CoroutineDispatcher,
    transactionModel: MutableValue<Transaction>,
    private val onNavigateToEditTransactionDetails: (transaction: Transaction) -> Unit,
    private val onDeleteTransaction: suspend (bucketId: UUID) -> Unit
) :
    TransactionDetailsComponent, ComponentContext by componentContext {

    override val transactionModel: MutableValue<Transaction> = transactionModel
    override fun navigateToEditTransactionDetails(transaction: Transaction) {
        onNavigateToEditTransactionDetails(transaction)
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        withContext(dispatcher) {
            database.transactionQueries.deleteTransaction(transaction.id)
        }
        onDeleteTransaction(transaction.bucketId)
    }

}