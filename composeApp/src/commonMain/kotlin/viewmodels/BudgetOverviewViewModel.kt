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
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import models.Bucket
import models.Container
import models.Transaction
import models.toApplicationDataModel
import models.toApplicationDataModelOfMonth
import models.toContainerList
import networking.BudgytHttpClient
import networking.repository.BucketRepositoryHttp
import networking.repository.TransactionRepositoryHttp
import repository.BucketRepository
import repository.BucketRepositoryImpl
import repository.TransactionRepository
import repository.TransactionRepositoryImpl
import java.util.UUID

interface BaseViewModel {
    val callstack: Value<ChildStack<*, Child>>
    val cache: Value<List<Container>>
    val store: budgyt

    fun onBackClicked(toIndex: Int)

    fun navigateToAddTransaction()

    fun navigateToAddBucket()

    suspend fun pullCacheFromRemoteEndpoint()

    sealed class Child {
        class ListChild(val component: ListComponent) : Child()
        class BucketDetailsChild(val component: DetailsComponent) : Child()

        class AddTransactionChild(val component: EditTransactionComponent) : Child()

        class TransactionDetailsChild(val component: TransactionDetailsComponent) : Child()

        class EditBucketChild(val component: EditBucketComponent) : Child()
    }
}

class BudgetOverviewViewModel(
    componentContext: ComponentContext,
    database: budgyt,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl(
        database,
        coroutineDispatcher
    ),
    private val bucketRepository: BucketRepository = BucketRepositoryImpl(
        database,
        coroutineDispatcher
    ),
    httpClient: HttpClient = BudgytHttpClient
) : BaseViewModel,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()

    private val currentDate = MutableValue(Clock.System.todayIn(TimeZone.currentSystemDefault()))
    override val cache = MutableValue(emptyList<Container>())

    override val store by lazy { database }

    private val transactionRepositoryHttp = TransactionRepositoryHttp(httpClient)
    private val bucketRepositoryHttp = BucketRepositoryHttp(httpClient)

    init {
        updateCache()
    }

    override val callstack =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.ListConfig,
            handleBackButton = true,
            childFactory = ::child
        )

    override fun onBackClicked(toIndex: Int) {
        navigation.popTo(toIndex)
    }

    override suspend fun pullCacheFromRemoteEndpoint() {
        try {
            val currentCache = bucketRepository.getBuckets().getOrThrow()
            val result = bucketRepositoryHttp.getBuckets().getOrThrow()
            currentCache.forEach { bucket -> //Remove buckets that don't exist within our upstream source
                if (result.find { searchBucket -> searchBucket.id == bucket.id } == null) {
                    bucketRepository.deleteBucket(bucket.id)
                }
                val localTransactions = transactionRepository.getTransactionsForBucketId(bucket.id).getOrThrow()
                localTransactions.forEach {transaction ->  //Remove transactions that don't exist in our upstream
                    if(bucket.transactions.find { searchTransaction -> searchTransaction.id == transaction.id } == null) {
                        transactionRepository.deleteTransaction(transaction.id)
                    }
                }
            }
            for (bucket in result) {
                if (currentCache.find { searchBucket -> searchBucket.id == bucket.id } == null) { //Add bucket if it doesn't exist. Could be replicated with an UPSERT statement.
                    bucketRepository.addBucket(bucket)
                } else {
                    bucketRepository.editBucket(bucket)
                }
                val transactions =
                    transactionRepository.getTransactionsForBucketId(bucket.id).getOrThrow()
                bucket.transactions.forEach { transaction -> //Similar upsert logic to above
                    if (transactions.find { searchTransaction -> searchTransaction.id == transaction.id } == null) {
                        transactionRepository.addTransaction(transaction)
                    } else {
                        transactionRepository.updateTransaction(transaction)
                    }
                }
            }
            updateCache()
        } catch (exception: Exception) {
            println("Error while updating cache: $exception")
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): BaseViewModel.Child {
        return when (config) {
            is Config.ListConfig -> BaseViewModel.Child.ListChild(listComponent(componentContext))
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
            store.bucketQueries.getBuckets().executeAsList()
                .map { bucket ->
                    bucket.toApplicationDataModelOfMonth(
                        budgyt = store,
                        currentDate = currentDate.value
                    )
                }.toContainerList().sortedBy { container -> container.containerType }
        }
    }

    private suspend fun updateBucketInCallstack(bucketId: UUID) {
        callstack.items.forEach { item ->
            if (item.instance is BaseViewModel.Child.BucketDetailsChild) {
                val child = item.instance as BaseViewModel.Child.BucketDetailsChild
                withContext(coroutineDispatcher) {
                    child.component.bucketModel.update {
                        child.component.bucketModel.value.copy(
                            transactions = store.transactionQueries.getTransactionsForBucketId(
                                bucketId
                            ).executeAsList()
                                .map { budgetTransaction -> budgetTransaction.toApplicationDataModel() }
                        )
                    }
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
            transactionRepository = transactionRepository,
            transactionRepositoryHttp = transactionRepositoryHttp,
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
        return DefaultListComponent(
            componentContext = componentContext,
            model = cache,
            currentDate = currentDate,
            onItemSelected = { bucket ->
                navigation.push(configuration = Config.Details(bucket))
            },
            onUpdateCurrentDate = { month, year ->
                currentDate.update {
                    LocalDate(year = year, monthNumber = month, dayOfMonth = 1)
                }
                updateCache()
            }
        )
    }

    override fun navigateToAddTransaction() {
        navigation.push(configuration = Config.Add(item = null))
    }

    override fun navigateToAddBucket() {
        navigation.push(configuration = Config.AddEditBucket(bucket = null))
    }

    private fun editTransactionComponent(
        componentContext: ComponentContext,
        transaction: Transaction?
    ): EditTransactionComponent {
        return DefaultEditTransactionComponent(
            componentContext = componentContext,
            transactionRepository = transactionRepository,
            transactionRepositoryHttp = transactionRepositoryHttp,
            bucketRepository = bucketRepository,
            bucketRepositoryHttp = bucketRepositoryHttp,
            currentTransaction = transaction,
            onTransactionUpdated = { transactionEditType, transaction ->
                updateCache()
                navigation.pop()
                when (transactionEditType) {
                    TransactionEditType.CREATE -> {}
                    TransactionEditType.UPDATE -> {
                        val top = callstack.active.instance
                        if (top is BaseViewModel.Child.TransactionDetailsChild) {
                            top.component.transactionModel.update {
                                store.transactionQueries.getTransactionById(
                                    transaction.id
                                ).executeAsOne().toApplicationDataModel()
                            }
                        }
                        updateBucketInCallstack(transaction.bucketId)
                    }

                    TransactionEditType.DELETE -> {
                        updateBucketInCallstack(bucketId = transaction.bucketId)
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
            bucketRepository = bucketRepository,
            bucketRepositoryHttp = bucketRepositoryHttp,
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
            bucketRepository = bucketRepository,
            bucketRepositoryHttp = bucketRepositoryHttp,
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
        @Serializable
        data object ListConfig : Config

        @Serializable
        data class Details(val item: Bucket) : Config

        @Serializable
        data class TransactionDetails(val item: Transaction) : Config

        @Serializable
        data class Add(val item: Transaction?) : Config

        @Serializable
        data class AddEditBucket(val bucket: Bucket?) : Config
    }
}


