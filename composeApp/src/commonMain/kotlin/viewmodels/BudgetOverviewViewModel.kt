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
import models.Container
import models.Transaction
import models.toApplicationDataModel
import models.toContainerList
import java.util.UUID

val EXAMPLE_BUDGET = listOf(
    Transaction(
        id = UUID.randomUUID(),
        note = "First contribution of month",
        transactionAmount = 1277f,
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

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>
    val cache: Value<List<Container>>
    val store: budgyt

    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class ListChild(val component: ListComponent) : Child()
        class DetailsChild(val component: DetailsComponent) : Child()

        class AddTransactionChild(val component: TransactionComponent) : Child()

        class AddBucketChild(val component: AddBucketComponent) : Child()
    }
}

class BudgetOverviewViewModel(componentContext: ComponentContext, database: budgyt) : BaseViewModel,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val cache = MutableValue(emptyList<Container>())

    override val store by lazy { database }

    init {
        cache.update {
            store.bucketQueries.getBuckets().executeAsList()
                .map { bucket -> bucket.toApplicationDataModel(budgyt = store) }.toContainerList()
        }
    }

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
        return when (config) {
            is Config.List -> BaseViewModel.Child.ListChild(listComponent(componentContext))
            is Config.Details -> BaseViewModel.Child.DetailsChild(
                detailsComponent(
                    componentContext,
                    config
                )
            )

            is Config.Add -> BaseViewModel.Child.AddTransactionChild(
                transactionComponent(
                    componentContext,
                    config.item
                )
            )

            is Config.AddBucket -> BaseViewModel.Child.AddBucketChild(
                addBucketComponent(
                    componentContext
                )
            )
        }
    }

    private fun listComponent(componentContext: ComponentContext): ListComponent {
        return DefaultListComponent(componentContext = componentContext,
            model = cache,
            onItemSelected = { bucket ->
                navigation.push(configuration = Config.Details(bucket))
            },
            onAddTransactionSelected = { transaction ->
                navigation.push(configuration = Config.Add(transaction))
            },
            onAddBucketSelected = { navigation.push(configuration = Config.AddBucket) }
        )
    }

    private fun transactionComponent(
        componentContext: ComponentContext,
        transaction: Transaction?
    ): TransactionComponent {
        return DefaultTransactionComponent(
            componentContext = componentContext,
            database = store,
            currentTransaction = transaction,
            onTransactionUpdated = {
                cache.update {
                    store.bucketQueries.getBuckets().executeAsList()
                        .map { bucket -> bucket.toApplicationDataModel(budgyt = store) }
                        .toContainerList()
                }
                navigation.pop()
            }
        )
    }

    private fun detailsComponent(
        componentContext: ComponentContext,
        config: Config.Details
    ): DetailsComponent {
        return DefaultDetailsComponent(
            componentContext = componentContext,
            item = config.item,
            onFinished = navigation::pop
        )
    }

    private fun addBucketComponent(componentContext: ComponentContext): AddBucketComponent {
        return DefaultAddBucketComponent(
            componentContext = componentContext,
            database = store,
            onAddBucket = {
                cache.update {
                    store.bucketQueries.getBuckets().executeAsList()
                        .map { bucket -> bucket.toApplicationDataModel(budgyt = store) }
                        .toContainerList()
                }
                navigation.pop()
            }
        )
    }

    @Serializable
    sealed interface Config {
        data object List : Config
        data class Details(val item: Bucket) : Config

        data class Add(val item: Transaction?) : Config


        data object AddBucket : Config
    }
}


