package networking.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import models.Bucket
import models.Transaction
import networking.REMOTE_ENDPOINT
import repository.BucketRepository
import java.util.UUID

const val BUCKET_ENDPOINT = "$REMOTE_ENDPOINT/buckets"
class BucketRepositoryHttp(private val client: HttpClient): BucketRepository {
    override suspend fun addBucket(bucket: Bucket): Result<Bucket> {
        val response = client.request(BUCKET_ENDPOINT) {
            method = HttpMethod.Post
            setBody(bucket)
        }.body<Bucket>()
        return Result.success(response)
    }

    override suspend fun getBuckets(): Result<List<Bucket>> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Get
        }.body<List<Bucket>>()
        return Result.success(response)
    }

    override suspend fun editBucket(updatedBucket: Bucket): Result<Bucket> {
        val response = client.request(BUCKET_ENDPOINT) {
            method = HttpMethod.Patch
            setBody(updatedBucket)
        }.body<Bucket>()
        return Result.success(response)
    }

    override suspend fun deleteBucket(bucketId: UUID): Boolean {
        val response = client.request(BUCKET_ENDPOINT) {
            method = HttpMethod.Delete
            setBody(bucketId)
        }.body<Boolean>()
        return response
    }

    override suspend fun getBucketById(bucketId: UUID): Result<Bucket> {
        val response = client.request(TRANSACTION_ENDPOINT) {
            method = HttpMethod.Get
            parameter("bucketId", bucketId)
        }.body<Bucket>()
        return Result.success(response)
    }

}