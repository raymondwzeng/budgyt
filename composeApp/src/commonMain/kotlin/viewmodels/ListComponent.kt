package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import kotlinx.datetime.LocalDate
import models.Bucket
import models.Container

interface ListComponent {
    val model: MutableValue<List<Container>>
    val currentDate: MutableValue<LocalDate>
    fun onItemClicked(item: Bucket)
    fun updateCurrentDate(month: Int, year: Int)

}

class DefaultListComponent(
    componentContext: ComponentContext,
    override val currentDate: MutableValue<LocalDate>,
    override val model: MutableValue<List<Container>>,
    private val onItemSelected: (item: Bucket) -> Unit,
    private val onUpdateCurrentDate: (month: Int, year: Int) -> Unit
) : ListComponent, ComponentContext by componentContext {
    override fun onItemClicked(item: Bucket) {
        onItemSelected(item)
    }

    override fun updateCurrentDate(month: Int, year: Int) {
        onUpdateCurrentDate(month, year)
    }
}