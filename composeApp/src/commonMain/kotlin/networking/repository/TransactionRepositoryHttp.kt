package networking.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import kotlinx.datetime.LocalDate
import models.Transaction
import networking.REMOTE_ENDPOINT
import repository.TransactionRepository
import java.util.UUID

const val TRANSACTION_ENDPOINT = "$REMOTE_ENDPOINT/transactions"

class TransactionRepositoryHttp(private val client: HttpClient): TransactionRepository {
    override suspend fun addTransaction(transaction: Transaction): Result<Transaction> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }.body<Transaction>()
        return Result.success(response)
    }

    override suspend fun deleteTransaction(transactionId: UUID): Boolean {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Delete
            contentType(ContentType.Application.Json)
            setBody(transactionId)
        }.body<Boolean>()
        return response
    }

    override suspend fun updateTransaction(updatedTransaction: Transaction): Result<Transaction> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Patch
            contentType(ContentType.Application.Json)
            setBody(updatedTransaction)
        }.body<Transaction>()
        return Result.success(response)
    }

    override suspend fun getTransactionsForBucketId(bucketId: UUID): Result<List<Transaction>> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Get
            parameter("bucketId", bucketId)
        }.body<List<Transaction>>()
        return Result.success(response)
    }

    override suspend fun getTransactionsForBucketForRange(
        bucketId: UUID,
        fromDate: LocalDate,
        toDate: LocalDate
    ): Result<List<Transaction>> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Get
            parameter("bucketId", bucketId)
            parameter("fromDate", fromDate)
            parameter("toDate", toDate)
        }.body<List<Transaction>>()
        return Result.success(response)
    }

    override suspend fun getTransactionById(transactionId: UUID): Result<Transaction> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Get
            parameter("transactionId", transactionId)
        }.body<Transaction>()
        return Result.success(response)
    }

}