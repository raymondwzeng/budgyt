package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.technology626.budgyt.budgyt
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID

interface ListComponent {
    val model: MutableValue<List<Container>>
    fun onItemClicked(item: Bucket)
    fun onAddTransactionButtonClicked()

    fun navigateToAddBucketSelected()
}

class DefaultListComponent(
    componentContext: ComponentContext,
    override val model: MutableValue<List<Container>>,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onAddTransactionSelected: (transaction: Transaction?) -> Unit,
    private val onAddBucketSelected: () -> Unit,
) : ListComponent, ComponentContext by componentContext {
    override fun onItemClicked(item: Bucket) {
        onItemSelected(item)
    }

    override fun onAddTransactionButtonClicked() {
        onAddTransactionSelected(null)
    }

    override fun navigateToAddBucketSelected() {
        onAddBucketSelected()
    }
}