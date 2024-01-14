package networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

const val REMOTE_ENDPOINT = "http://localhost:8080"

val BudgytHttpClient = HttpClient(CIO) {
    expectSuccess = true
    install(ContentNegotiation) {
        json()
    }
}
