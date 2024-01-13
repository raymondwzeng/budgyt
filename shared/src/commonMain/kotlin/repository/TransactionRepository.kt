package repository

import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import models.Transaction
import models.toApplicationDataModel
import java.util.UUID


interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction): Result<Transaction>

    suspend fun deleteTransaction(transactionId: UUID): Boolean

    suspend fun updateTransaction(
        updatedTransaction: Transaction
    ): Result<Transaction>

    suspend fun getTransactionsForBucketId(bucketId: UUID): Result<List<Transaction>>

    suspend fun getTransactionsForBucketForRange(
        bucketId: UUID,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<List<Transaction>>

    suspend fun getTransactionById(transactionId: UUID): Result<Transaction>
}

class TransactionRepositoryImpl(val budgyt: budgyt, val coroutineDispatcher: CoroutineDispatcher) :
    TransactionRepository {
    override suspend fun addTransaction(
        transaction: Transaction
    ): Result<Transaction> {
        return withContext(coroutineDispatcher) {
            budgyt.transactionQueries.addTransaction(
                id = transaction.id,
                transaction_amount = transaction.transactionAmount,
                transaction_note = transaction.note,
                transaction_date = transaction.transactionDate,
                bucket_id = transaction.bucketId
            )
            return@withContext budgyt.transactionQueries.getTransactionById(transaction.id)
                .executeAsOneOrNull()?.let { transaction ->
                    Result.success(transaction.toApplicationDataModel())
                }
                ?: Result.failure(Exception("Failed to insert new transaction into database. Transaction: $transaction"))
        }
    }

    override suspend fun deleteTransaction(transactionId: UUID): Boolean {
        return withContext(coroutineDispatcher) {
            budgyt.transactionQueries.deleteTransaction(id = transactionId)
            return@withContext budgyt.transactionQueries.getTransactionById(id = transactionId)
                .executeAsOneOrNull()?.let {
                    false
                } ?: true
        }
    }

    override suspend fun updateTransaction(
        updatedTransaction: Transaction
    ): Result<Transaction> {
        return withContext(coroutineDispatcher) {
            budgyt.transactionQueries.updateTransaction(
                transaction_amount = updatedTransaction.transactionAmount,
                transaction_note = updatedTransaction.note,
                transaction_date = updatedTransaction.transactionDate,
                bucket_id = updatedTransaction.bucketId,
                id = updatedTransaction.id
            )
            return@withContext budgyt.transactionQueries.getTransactionById(id = updatedTransaction.id)
                .executeAsOneOrNull()?.let { transaction ->
                    Result.success(transaction.toApplicationDataModel())
                }
                ?: Result.failure(Exception("Failed to update new transaction into database for transaction $updatedTransaction."))
        }
    }

    override suspend fun getTransactionsForBucketId(bucketId: UUID): Result<List<Transaction>> {
        return withContext(coroutineDispatcher) {
            return@withContext Result.success(
                budgyt.transactionQueries.getTransactionsForBucketId(
                    bucket_id = bucketId
                ).executeAsList().map { transaction ->
                    transaction.toApplicationDataModel()
                })
        }
    }

    override suspend fun getTransactionsForBucketForRange(
        bucketId: UUID,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<List<Transaction>> {
        return withContext(coroutineDispatcher) {
            return@withContext Result.success(
                budgyt.transactionQueries.getTransactionsForBucketForRange(
                    bucket_id = bucketId,
                    transaction_date = fromDate,
                    transaction_date_ = toDate
                ).executeAsList().map { transaction ->
                    transaction.toApplicationDataModel()
                }
            )
        }
    }

    override suspend fun getTransactionById(transactionId: UUID): Result<Transaction> {
        return withContext(coroutineDispatcher) {
            val transaction = budgyt.transactionQueries.getTransactionById(transactionId).executeAsOneOrNull()
            if(transaction == null) {
                return@withContext Result.failure(Throwable("Unable to find transaction with that ID $transactionId"))
            } else {
                return@withContext Result.success(transaction.toApplicationDataModel())
            }
        }
    }

}