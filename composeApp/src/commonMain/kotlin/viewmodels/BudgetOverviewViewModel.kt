package viewmodels

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.items
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.technology626.budgyt.budgyt
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import models.Bucket
import models.Container
import models.Transaction
import models.toApplicationDataModel
import models.toApplicationDataModelOfMonth
import models.toContainerList
import java.util.UUID

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>
    val cache: Value<Array<List<Container>>>
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

    private val currentDate = MutableValue(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    override val cache = MutableValue(arrayOf(emptyList<Container>(), emptyList(), emptyList()))

    override val store by lazy { database }

    init {
        updateCache()
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
                    componentContext = componentContext,
                    bucket = config.bucket
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
            val clone = cache.value.clone()
            clone[1] = store.bucketQueries.getBuckets().executeAsList()
                .map { bucket ->
                    bucket.toApplicationDataModelOfMonth(
                        budgyt = store,
                        currentDate = currentDate.value
                    )
                }
                .toContainerList()
            clone[0] = store.bucketQueries.getBuckets().executeAsList()
                .map { bucket ->
                    bucket.toApplicationDataModelOfMonth(
                        budgyt = store,
                        currentDate = currentDate.value.minus(1, DateTimeUnit.MONTH)
                    )
                }.toContainerList()
            clone
        }
    }

    private fun updateBucketInCallstack(bucketId: UUID) {
        callstack.items.forEach { item ->
            if (item.instance is BaseViewModel.Child.BucketDetailsChild) {
                (item.instance as BaseViewModel.Child.BucketDetailsChild).component.bucketModel.update {
                    (item.instance as BaseViewModel.Child.BucketDetailsChild).component.bucketModel.value.copy(
                        transactions = store.transactionQueries.getTransactionsForBucketId(
                            bucketId
                        ).executeAsList()
                            .map { budgetTransaction -> budgetTransaction.toApplicationDataModel() }
                    )
                }
            }
        }
    }

    private fun transactionDetailsComponent(
        componentContext: ComponentContext,
        transaction: Transaction
    ): TransactionDetailsComponent {
        return DefaultTransactionDetailsComponent(
            componentContext = componentContext,
            database = store,
            transactionModel = MutableValue(transaction),
            onDeleteTransaction = { bucketId ->
                updateCache()
                updateBucketInCallstack(bucketId)
                navigation.pop()
            },
            onNavigateToEditTransactionDetails = { selectedTransaction ->
                navigation.push(Config.Add(selectedTransaction))
            }
        )
    }

    private fun listComponent(componentContext: ComponentContext): ListComponent {
        return DefaultListComponent(componentContext = componentContext,
            model = MutableValue(cache.value[1]),
            onItemSelected = { bucket ->
                navigation.push(configuration = Config.Details(bucket))
            },
            onAddTransactionSelected = { transaction ->
                navigation.push(configuration = Config.Add(transaction))
            },
            onAddBucketSelected = { navigation.push(configuration = Config.AddEditBucket(bucket = null)) }
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
            onTransactionUpdated = { transactionEditType, transactionId, bucketId ->
                updateCache()
                navigation.pop()
                when (transactionEditType) {
                    TransactionEditType.CREATE -> {}
                    TransactionEditType.UPDATE -> {
                        val top = callstack.active.instance
                        if (top is BaseViewModel.Child.TransactionDetailsChild) {
                            top.component.transactionModel.update {
                                store.transactionQueries.getTransactionById(
                                    transactionId
                                ).executeAsOne().toApplicationDataModel()
                            }
                        }
                        updateBucketInCallstack(bucketId)
                    }

                    TransactionEditType.DELETE -> {
                        updateBucketInCallstack(bucketId = bucketId)
                        navigation.pop()
                    }
                }
            }
        )
    }

    private fun bucketDetailsComponent(
        componentContext: ComponentContext,
        config: Config.Details
    ): DetailsComponent {
        return DefaultDetailsComponent(
            componentContext = componentContext,
            item = MutableValue(config.item),
            database = store,
            onFinished = {
                updateCache()
                navigation.pop()
            },
            onNavigateToTransactionDetails = { transaction ->
                navigation.push(configuration = Config.TransactionDetails(transaction))
            },
            onNavigateToEditBucket = { bucket ->
                navigation.push(configuration = Config.AddEditBucket(bucket))
            }
        )
    }

    private fun editBucketComponent(
        bucket: Bucket?,
        componentContext: ComponentContext
    ): EditBucketComponent {
        return DefaultEditBucketComponent(
            componentContext = componentContext,
            bucket = bucket,
            database = store,
            onAddBucket = { bucketId ->
                updateCache()
                navigation.pop()
                if (callstack.active.instance is BaseViewModel.Child.BucketDetailsChild) {
                    (callstack.active.instance as BaseViewModel.Child.BucketDetailsChild).component.bucketModel.update {
                        store.bucketQueries.getBucketById(bucketId).executeAsOne()
                            .toApplicationDataModel(budgyt = store)
                    }
                }
            }
        )
    }

    @Serializable
    sealed interface Config {
        data object List : Config
        data class Details(val item: Bucket) : Config

        data class TransactionDetails(val item: Transaction) : Config

        data class Add(val item: Transaction?) : Config


        data class AddEditBucket(val bucket: Bucket?) : Config
    }
}


