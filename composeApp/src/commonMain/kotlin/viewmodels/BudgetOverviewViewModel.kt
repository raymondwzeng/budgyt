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

        class AddTransactionChild(val component: ListComponent) : Child()

        class AddBucketChild(val component: AddBucketComponent) : Child()
    }
}
class BudgetOverviewViewModel(componentContext: ComponentContext, database: budgyt) : BaseViewModel,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    override val cache = MutableValue(emptyList<Container>())

    init {
        val testBucketId = UUID.randomUUID()
        println("Adding sample data")
        database.bucketQueries.addBucket(id = testBucketId, bucket_name = "401k Contributions", bucket_type = BucketType.INFLOW, bucket_estimate = 1766.0)
        database.transactionQueries.addTransaction(id = EXAMPLE_BUDGET.first().id, transaction_amount = EXAMPLE_BUDGET.first().transactionAmount.toDouble(), transaction_note = "", transaction_date = EXAMPLE_BUDGET.first().transactionDate, bucket_id = testBucketId)
        println("Querying database")
        database.bucketQueries.getBuckets().executeAsList().map { buckets -> buckets.toApplicationDataModel(budgyt = database) }
        cache.update {
            database.bucketQueries.getBuckets().executeAsList().map { bucket -> bucket.toApplicationDataModel(budgyt = database) }.toContainerList()
      }
        println("Cache is now ${cache.value}")
    }


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
        return when (config) {
            is Config.List -> BaseViewModel.Child.ListChild(listComponent(componentContext))
            is Config.Details -> BaseViewModel.Child.DetailsChild(
                detailsComponent(
                    componentContext,
                    config
                )
            )

            is Config.Add -> BaseViewModel.Child.AddTransactionChild(listComponent(componentContext))
            is Config.AddBucket -> BaseViewModel.Child.AddBucketChild(
                addBucketComponent(
                    componentContext
                )
            )
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
            },
            onAddBucketSelected = { navigation.push(configuration = Config.AddBucket) }
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
            containerState = cache,
            onAddBucket = { newContainerList ->
                cache.update {
                    newContainerList
                }
                navigation.pop()
            }
        )
    }

    @Serializable
    sealed interface Config {
        data object List : Config
        data class Details(val item: Bucket) : Config

        data object Add : Config

        data object AddBucket : Config
    }
}


