package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import models.Bucket

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>

    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class ListChild(val component: ListComponent): Child()
        class DetailsChild(val component: DetailsComponent): Child()
    }
}

class BudgetOverviewViewModel(componentContext: ComponentContext): BaseViewModel, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val callstack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::child
        )

    override fun onBackClicked(toIndex: Int) {
        navigation.popTo(toIndex)
    }

    private fun child(config: Config, componentContext: ComponentContext): BaseViewModel.Child {
        return when(config) {
            is Config.List -> BaseViewModel.Child.ListChild(listComponent(componentContext))
            is Config.Details -> BaseViewModel.Child.DetailsChild(detailsComponent(componentContext, config))
        }
    }

    private fun listComponent(componentContext: ComponentContext): ListComponent {
        return DefaultListComponent(componentContext) { bucket ->
            navigation.push(configuration = Config.Details(bucket))
        }
    }

    private fun detailsComponent(componentContext: ComponentContext, config: Config.Details): DetailsComponent {
        return DefaultDetailsComponent(
            componentContext = componentContext,
            item = config.item,
            onFinished = navigation::pop
        )
    }

    @Serializable
    sealed interface Config {
        data object List: Config
        data class Details(val item: Bucket): Config
    }
}
