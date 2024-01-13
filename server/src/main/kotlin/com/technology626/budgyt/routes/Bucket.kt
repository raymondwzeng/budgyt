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
import models.Bucket
import repository.BucketRepository
import java.util.UUID

fun Route.buckets(repository: BucketRepository) {
    route("/buckets") {
        get("{id?}") {
            try {
                val bucketId = UUID.fromString(call.parameters["id"])
                repository.getBucketById(bucketId).onSuccess { bucket ->
                    call.respond(bucket)
                }.onFailure { error ->
                    call.respond(
                        HttpStatusCode.BadRequest,
                        error.message ?: "Unable to find bucket with that UUID."
                    )
                }
            } catch (exception: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid UUID")
            }
        }
        get {
            repository.getBuckets().onSuccess { bucketList ->
                call.respond(bucketList)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "Failed to retrieve buckets.")
            }
        }
        patch {
            val updatedBucket = call.receive<Bucket>()
            repository.editBucket(updatedBucket).onSuccess { bucket ->
                call.respond(bucket)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "Failed to updated bucket $updatedBucket")
            }
        }
        delete {
            val bucketId = call.receive<UUID>()
            val result = repository.deleteBucket(bucketId)
            if(result) {
                call.respond(true)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to delete bucket of ID $bucketId")
            }
        }
        post {
            val newBucket = call.receive<Bucket>()
            repository.addBucket(newBucket).onSuccess { bucket ->
                call.respond(bucket)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, "Failed to add new bucket $newBucket")
            }
        }
    }
}