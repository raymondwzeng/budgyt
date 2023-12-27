package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.technology626.budgyt.budgyt
import models.Transaction
import java.util.UUID

interface TransactionDetailsComponent {
    val transactionModel: Value<Transaction>
    fun navigateToEditTransactionDetails(transaction: Transaction)

    fun deleteTransaction(transactionId: UUID)
}

class DefaultTransactionDetailsComponent(
    componentContext: ComponentContext,
    private val database: budgyt,
    transactionModel: Value<Transaction>,
    private val onNavigateToEditTransactionDetails: (transaction: Transaction) -> Unit,
    private val onDeleteTransaction: () -> Unit
) :
    TransactionDetailsComponent, ComponentContext by componentContext {

    override val transactionModel: Value<Transaction> = MutableValue(transactionModel.value)
    override fun navigateToEditTransactionDetails(transaction: Transaction) {
        onNavigateToEditTransactionDetails(transaction)
    }

    override fun deleteTransaction(transactionId: UUID) {
        database.transactionQueries.deleteTransaction(transactionId)
        onDeleteTransaction()
    }

}