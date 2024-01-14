package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Transaction
import networking.repository.TransactionRepositoryHttp
import repository.TransactionRepository
import java.util.UUID

interface TransactionDetailsComponent {
    val transactionModel: MutableValue<Transaction>
    fun navigateToEditTransactionDetails(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}

class DefaultTransactionDetailsComponent(
    componentContext: ComponentContext,
    private val transactionRepository: TransactionRepository,
    private val transactionRepositoryHttp: TransactionRepositoryHttp,
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
        transactionRepository.deleteTransaction(transaction.id)
        try {
            transactionRepositoryHttp.deleteTransaction(transaction.id)
        } catch(exception: Exception) {
            println("EXCEPTION: ${exception.message}")
        }
        onDeleteTransaction(transaction.bucketId)
    }

}