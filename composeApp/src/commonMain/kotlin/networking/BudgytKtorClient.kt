package networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

const val REMOTE_ENDPOINT = "localhost:8080"

val BudgytHttpClient = HttpClient(CIO) {
    expectSuccess = true
}
