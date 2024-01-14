package networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val BudgytHttpClient = HttpClient(CIO) {
    expectSuccess = true
    install(ContentNegotiation) {
        json()
    }
}
