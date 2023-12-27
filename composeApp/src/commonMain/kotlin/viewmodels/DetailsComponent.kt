package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import models.Bucket
import models.Transaction
import java.util.UUID

interface DetailsComponent {
    val model: Value<Bucket>

    fun navigateToTransactionDetail(transaction: Transaction)
    fun onFinished()
}

class DefaultDetailsComponent(
    componentContext: ComponentContext,
    item: Bucket,
    private val onFinished: () -> Unit,
    private val onNavigateToTransactionDetails: (transaction: Transaction) -> Unit
): DetailsComponent, ComponentContext by componentContext {
    override val model: Value<Bucket> = MutableValue(item)
    override fun navigateToTransactionDetail(transaction: Transaction) {
        onNavigateToTransactionDetails(transaction)
    }

    override fun onFinished() {
        onFinished
    }
}