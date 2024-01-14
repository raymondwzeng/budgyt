package repository

import com.technology626.budgyt.budgyt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import models.Bucket
import models.toApplicationDataModel
import java.util.UUID

interface BucketRepository {
    suspend fun addBucket(bucket: Bucket): Result<Bucket>

    suspend fun getBuckets(): Result<List<Bucket>>

    suspend fun editBucket(updatedBucket: Bucket): Result<Bucket>

    suspend fun deleteBucket(bucketId: UUID): Boolean

    suspend fun getBucketById(bucketId: UUID): Result<Bucket>
}

class BucketRepositoryImpl(private val budgyt: budgyt, private val coroutineDispatcher: CoroutineDispatcher) :
    BucketRepository {
    override suspend fun addBucket(bucket: Bucket): Result<Bucket> {
        return withContext(coroutineDispatcher) {
            budgyt.bucketQueries.addBucket(
                id = bucket.id,
                bucket_name = bucket.bucketName,
                bucket_type = bucket.bucketType,
                bucket_estimate = bucket.estimatedAmount
            )
            return@withContext budgyt.bucketQueries.getBucketById(bucket.id).executeAsOneOrNull()
                ?.let { bucket ->
                    Result.success(bucket.toApplicationDataModel(budgyt))
                } ?: Result.failure(Exception("Failed to add new bucket $bucket."))
        }
    }

    override suspend fun getBuckets(): Result<List<Bucket>> {
        return withContext(coroutineDispatcher) {
            return@withContext Result.success(
                budgyt.bucketQueries.getBuckets().executeAsList()
                    .map { bucket -> bucket.toApplicationDataModel(budgyt) })
        }
    }

    override suspend fun editBucket(updatedBucket: Bucket): Result<Bucket> {
        return withContext(coroutineDispatcher) {
            budgyt.bucketQueries.editBucket(
                bucket_name = updatedBucket.bucketName,
                bucket_type = updatedBucket.bucketType,
                bucket_estimate = updatedBucket.estimatedAmount,
                id = updatedBucket.id
            )
            return@withContext budgyt.bucketQueries.getBucketById(updatedBucket.id)
                .executeAsOneOrNull()?.let { bucket ->
                Result.success(bucket.toApplicationDataModel(budgyt))
            } ?: Result.failure(Exception("Failed to update bucket $updatedBucket"))
        }
    }

    override suspend fun deleteBucket(bucketId: UUID): Boolean {
        return withContext(coroutineDispatcher) {
            budgyt.bucketQueries.deleteBucket(bucketId)

            return@withContext budgyt.bucketQueries.getBucketById(bucketId).executeAsOneOrNull()
                ?.let {
                    false
                } ?: true
        }
    }

    override suspend fun getBucketById(bucketId: UUID): Result<Bucket> {
        return withContext(coroutineDispatcher) {
            budgyt.bucketQueries.getBucketById(bucketId).executeAsOneOrNull()?.let { bucket ->
                Result.success(bucket.toApplicationDataModel(budgyt))
            } ?: Result.failure(Exception("Unable to find bucket of id $bucketId"))
        }
    }

}