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
import models.Transaction
import repository.TransactionRepository
import java.util.UUID

fun Route.transactions(repository: TransactionRepository) {
    route("/transactions") {
        get("{id?}") {
            try {
                val transactionId = UUID.fromString(call.parameters["id"])
                repository.getTransactionById(transactionId).onSuccess { transaction ->
                    call.respond(transaction)
                }.onFailure { error ->
                    call.respond(HttpStatusCode.BadRequest, error.message ?: "Bad request")
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
            if(result) {
                call.respond(true)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to delete transaction of UUID $transactionUUID")
            }
        }
        patch {
            val updatedTransaction = call.receive<Transaction>()
            repository.updateTransaction(updatedTransaction).onSuccess { newTransaction ->
                call.respond(newTransaction)
            }.onFailure {  error ->
                call.respond(HttpStatusCode.BadRequest, error.message ?: "Failed to update transaction $updatedTransaction")
            }
        }
    }
}