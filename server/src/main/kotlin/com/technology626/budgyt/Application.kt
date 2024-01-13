package com.technology626.budgyt

import DriverFactory
import SERVER_PORT
import com.technology626.budgyt.routes.buckets
import com.technology626.budgyt.routes.transactions
import createDatabase
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.Routing
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import repository.BucketRepositoryImpl
import repository.TransactionRepositoryImpl

object CoreModule {
    val DISPATCHER_IO = Dispatchers.IO
    val budgyt = createDatabase(DriverFactory())
}

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

internal fun Routing.registerRoutes() {
    transactions(TransactionRepositoryImpl(budgyt = CoreModule.budgyt, coroutineDispatcher = CoreModule.DISPATCHER_IO))
    buckets(BucketRepositoryImpl(budgyt = CoreModule.budgyt, coroutineDispatcher = CoreModule.DISPATCHER_IO))
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        registerRoutes()
    }
}
