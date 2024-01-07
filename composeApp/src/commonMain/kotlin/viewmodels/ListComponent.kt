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
    val model: MutableValue<Array<List<Container>>>
    val currentDate: MutableValue<LocalDate>
    fun onItemClicked(item: Bucket)
    fun onAddTransactionButtonClicked()

    fun navigateToAddBucketSelected()

    fun changeMonth(oldPageNumber: Int, newPageNumber: Int)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    override val currentDate: MutableValue<LocalDate>,
    override val model: MutableValue<Array<List<Container>>>,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onAddTransactionSelected: (transaction: Transaction?) -> Unit,
    private val onAddBucketSelected: () -> Unit,
    private val onMonthChanged: (LocalDate) -> Unit
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

    override fun changeMonth(oldPageNumber: Int, newPageNumber: Int) {
        val newMonth: LocalDate = when(newPageNumber - oldPageNumber) {
            1 -> currentDate.value.plus(1, DateTimeUnit.MONTH)
            -1 -> currentDate.value.minus(1, DateTimeUnit.MONTH)
            else -> currentDate.value
        }
        onMonthChanged(newMonth)
    }
}