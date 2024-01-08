package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.technology626.budgyt.budgyt
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID

interface ListComponent {
    val model: MutableValue<List<Container>>
    val currentDate: MutableValue<LocalDate>
    fun onItemClicked(item: Bucket)
    fun onAddTransactionButtonClicked()

    fun navigateToAddBucketSelected()

    fun updateCurrentDate(month: Int, year: Int)

}

class DefaultListComponent(
    componentContext: ComponentContext,
    override val currentDate: MutableValue<LocalDate>,
    override val model: MutableValue<List<Container>>,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onAddTransactionSelected: (transaction: Transaction?) -> Unit,
    private val onAddBucketSelected: () -> Unit,
    private val onUpdateCurrentDate: (month: Int, year: Int) -> Unit
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

    override fun updateCurrentDate(month: Int, year: Int) {
        onUpdateCurrentDate(month, year)
    }
}