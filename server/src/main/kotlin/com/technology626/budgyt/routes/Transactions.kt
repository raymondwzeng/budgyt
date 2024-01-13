package com.technology626.budgyt.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
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
            call.respond("TODO - make route work")
        }
        delete {
            call.respond("TODO - make route work")
        }
        patch {
            call.respond("TODO - make route work")
        }
    }
}