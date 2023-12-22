package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.technology626.budgyt.budgyt
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import models.Bucket
import models.BucketType
import models.Container
import models.Transaction
import java.util.UUID

val EXAMPLE_BUDGET = listOf(
    Transaction(
        id = UUID.randomUUID(),
        note = "Test note",
        transactionAmount = 12.2f,
        transactionDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
)

val EXAMPLE_BUDGET_2 = listOf(
    Transaction(
        id = UUID.randomUUID(),
        note = "Test note 2",
        transactionAmount = 42.3f,
        transactionDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
)


val EXAMPLE_BUCKET = Bucket(
    id = UUID.randomUUID(),
    bucketName = "Example Bucket",
    transactions = EXAMPLE_BUDGET
)

val EXAMPLE_BUCKET_2 = Bucket(
    id = UUID.randomUUID(),
    bucketName = "Example Bucket 2",
    transactions = EXAMPLE_BUDGET_2
)

val EXAMPLE_CONTAINER = Container(
    containerType = BucketType.OUTFLOW,
    buckets = mapOf(
        EXAMPLE_BUCKET.id to EXAMPLE_BUCKET,
        EXAMPLE_BUCKET_2.id to EXAMPLE_BUCKET_2)
)

val EMPTY_INFLOW_CONTAINER = Container(
    containerType = BucketType.INFLOW,
    buckets = emptyMap()
)

val EMPTY_FUND_CONTAINER = Container(
    containerType = BucketType.FUND,
    buckets = emptyMap()
)

val EXAMPLE_CONTAINERS = listOf(EMPTY_INFLOW_CONTAINER, EXAMPLE_CONTAINER, EMPTY_FUND_CONTAINER)

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>
    val cache: Value<List<Container>>
    val store: budgyt

    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class ListChild(val component: ListComponent): Child()
        class DetailsChild(val component: DetailsComponent): Child()

        class AddTransactionChild(val component: ListComponent): Child()
    }
}

class BudgetOverviewViewModel(componentContext: ComponentContext, database: budgyt): BaseViewModel, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val cache = MutableValue(EXAMPLE_CONTAINERS)

    override val callstack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.List,
            handleBackButton = true,
            childFactory = ::child
        )

    override val store = database

    override fun onBackClicked(toIndex: Int) {
        navigation.popTo(toIndex)
    }

    private fun child(config: Config, componentContext: ComponentContext): BaseViewModel.Child {
        return when(config) {
            is Config.List -> BaseViewModel.Child.ListChild(listComponent(componentContext))
            is Config.Details -> BaseViewModel.Child.DetailsChild(detailsComponent(componentContext, config))
            is Config.Add -> BaseViewModel.Child.AddTransactionChild(listComponent(componentContext))
        }
    }

    private fun listComponent(componentContext: ComponentContext): ListComponent {
        return DefaultListComponent(componentContext, onItemSelected = { bucket ->
            navigation.push(configuration = Config.Details(bucket))
        }, onAddTransactionSelected = {
            navigation.push(configuration = Config.Add)
        },
            containerState = cache,
            onTransactionAdded = { newContainerList ->
                cache.update {
                    newContainerList
                }
                navigation.pop()
            }
        )
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

        data object Add: Config
    }
}

