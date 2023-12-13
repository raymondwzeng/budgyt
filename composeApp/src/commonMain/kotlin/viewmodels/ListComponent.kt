package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import models.Bucket
import models.Container

interface ListComponent {
    val model: Value<List<Container>>
    fun onItemClicked(item: Bucket)
}

class DefaultListComponent(
    componentContext: ComponentContext,
    private val onItemSelected: (item: Bucket) -> Unit
): ListComponent, ComponentContext by componentContext {
    override val model = MutableValue(emptyList<Container>())

    override fun onItemClicked(item: Bucket) {
        onItemSelected(item)
    }
}