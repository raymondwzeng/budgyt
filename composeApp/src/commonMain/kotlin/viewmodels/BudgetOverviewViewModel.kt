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
import kotlinx.serialization.Serializable
import models.Bucket
import models.Container
import models.Transaction
import models.toApplicationDataModel
import models.toContainerList

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>
    val cache: Value<List<Container>>
    val store: budgyt

    fun onBackClicked(toIndex: Int)

    sealed class Child {
        class ListChild(val component: ListComponent) : Child()
        class BucketDetailsChild(val component: DetailsComponent) : Child()

        class AddTransactionChild(val component: EditTransactionComponent) : Child()

        class TransactionDetailsChild(val component: TransactionDetailsComponent) : Child()

        class EditBucketChild(val component: EditBucketComponent) : Child()
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
            is Config.Details -> BaseViewModel.Child.BucketDetailsChild(
                bucketDetailsComponent(
                    componentContext,
                    config
                )
            )

            is Config.Add -> BaseViewModel.Child.AddTransactionChild(
                editTransactionComponent(
                    componentContext,
                    config.item
                )
            )

            is Config.AddEditBucket -> BaseViewModel.Child.EditBucketChild(
                editBucketComponent(
                    componentContext
                )
            )

            is Config.TransactionDetails -> BaseViewModel.Child.TransactionDetailsChild(
                transactionDetailsComponent(
                    componentContext,
                    config.item
                )
            )
        }
    }

    private fun updateCache() {
        cache.update {
            store.bucketQueries.getBuckets().executeAsList()
                .map { bucket -> bucket.toApplicationDataModel(budgyt = store) }
                .toContainerList()
        }
    }

    private fun transactionDetailsComponent(
        componentContext: ComponentContext,
        transaction: Transaction
    ): TransactionDetailsComponent {
        return DefaultTransactionDetailsComponent(
            componentContext = componentContext,
            database = store,
            transactionModel = MutableValue(transaction), //TODO: This probably binds improperly
            onDeleteTransaction = {
                updateCache()
                navigation.pop()
            },
            onNavigateToEditTransactionDetails = { selectedTransaction ->
                navigation.push(Config.Add(selectedTransaction))
            }
        )
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
            onAddBucketSelected = { navigation.push(configuration = Config.AddEditBucket) }
        )
    }

    private fun editTransactionComponent(
        componentContext: ComponentContext,
        transaction: Transaction?
    ): EditTransactionComponent {
        return DefaultEditTransactionComponent(
            componentContext = componentContext,
            database = store,
            currentTransaction = transaction,
            onTransactionUpdated = {
                updateCache()
                navigation.pop()
            }
        )
    }

    private fun bucketDetailsComponent(
        componentContext: ComponentContext,
        config: Config.Details
    ): DetailsComponent {
        return DefaultDetailsComponent(
            componentContext = componentContext,
            item = config.item,
            onFinished = navigation::pop,
            onNavigateToTransactionDetails = { transaction ->
                navigation.push(configuration = Config.TransactionDetails(transaction))
            }
        )
    }

    private fun editBucketComponent(componentContext: ComponentContext): EditBucketComponent {
        return DefaultEditBucketComponent(
            componentContext = componentContext,
            database = store,
            onAddBucket = {
                updateCache()
                navigation.pop()
            }
        )
    }

    @Serializable
    sealed interface Config {
        data object List : Config
        data class Details(val item: Bucket) : Config

        data class TransactionDetails(val item: Transaction) : Config

        data class Add(val item: Transaction?) : Config


        data object AddEditBucket : Config
    }
}


