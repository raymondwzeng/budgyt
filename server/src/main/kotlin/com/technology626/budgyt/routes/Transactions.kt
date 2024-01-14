package com.technology626.budgyt.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlinx.datetime.LocalDate
import models.Transaction
import repository.TransactionRepository
import java.util.UUID

fun Route.transactions(repository: TransactionRepository) {
    route("/transactions") {
        get {
            try {
                val bucketIdString = call.request.queryParameters["bucketId"]
                val fromDateString = call.request.queryParameters["fromDate"]
                val toDateString = call.request.queryParameters["toDate"]
                if (bucketIdString == null) { //Standard request to get transaction by id
                    val transactionId =
                        UUID.fromString(call.request.queryParameters.getOrFail("transactionId")) //At this point, we must have a transaction because we are for sure not checking for bucketId/dateRange
                    repository.getTransactionById(transactionId).onSuccess { transaction ->
                        call.respond(transaction)
                    }.onFailure { error ->
                        call.respond(HttpStatusCode.BadRequest, error.message ?: "Bad request")
                    }
                } else {
                    val bucketId = UUID.fromString(bucketIdString)
                    if (fromDateString == null || toDateString == null) {
                        repository.getTransactionsForBucketId(bucketId)
                            .onSuccess { transactionList ->
                                call.respond(transactionList)
                            }.onFailure { error ->
                            call.respond(HttpStatusCode.BadRequest,
                                error.message
                                    ?: "Unable to retrieve transactions for bucket id $bucketId"
                            )
                        }
                    } else {
                        val fromDate = LocalDate.parse(fromDateString)
                        val toDate = LocalDate.parse(toDateString)
                        repository.getTransactionsForBucketForRange(bucketId, fromDate, toDate)
                            .onSuccess { transactionList ->
                                call.respond(transactionList)
                            }.onFailure { error ->
                                call.respond(HttpStatusCode.BadRequest, error.message ?: "Unable to retrieve transactions for bucket id $bucketId between $fromDate and $toDate.")
                        }
                    }
                }
            } catch (exception: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid UUID.")
            }
        }
        post {
            val transaction = call.receive<Transaction>()
            repository.addTransaction(transaction).onSuccess { newTransaction ->
                call.respond(newTransaction)
            }.onFailure { error ->
                call.respond(HttpStatusCode.BadRequest, error.message ?: "Bad request")
            }
        }
        delete {
            val transactionUUID = call.receive<UUID>()
            val result = repository.deleteTransaction(transactionUUID)
            if (result) {
                call.respond(true)
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Failed to delete transaction of UUID $transactionUUID"
                )
            }
        }
        patch {
            val updatedTransaction = call.receive<Transaction>()
            repository.updateTransaction(updatedTransaction).onSuccess { newTransaction ->
                call.respond(newTransaction)
            }.onFailure { error ->
                call.respond(
                    HttpStatusCode.BadRequest,
                    error.message ?: "Failed to update transaction $updatedTransaction"
                )
            }
        }
    }
}