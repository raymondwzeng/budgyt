package models

import com.technology626.budgyt.Bucket
import com.technology626.budgyt.budgyt
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import java.time.Month
import java.util.UUID

enum class BucketType {
    INFLOW, OUTFLOW, FUND
}

@Serializable
data class Bucket(
    @Serializable(with = JavaUUIDSerializer::class)
    val id: UUID,
    val bucketName: String,
    val estimatedAmount: Float,
    val transactions: List<Transaction>,
    val bucketType: BucketType
)

fun Bucket.toApplicationDataModel(budgyt: budgyt): models.Bucket {
    return Bucket(
        id = this.id,
        bucketName = bucket_name,
        estimatedAmount = bucket_estimate.toFloat(),
        bucketType = bucket_type, //TODO: Make coroutine context actually work
        transactions = budgyt.transactionQueries.getTransactionsForBucketId(this.id).executeAsList()
            .map { budgetTransaction -> budgetTransaction.toApplicationDataModel() }
    )
}

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

fun Bucket.toApplicationDataModelOfMonth(budgyt: budgyt, currentDate: LocalDate): models.Bucket {
    val lowEnd = LocalDate(currentDate.year, currentDate.month, 1)
    val highEnd = LocalDate(
        currentDate.year, currentDate.month, currentDate.month.length(
            isLeapYear(currentDate.year)
        )
    )
    println("Checking for transactions between $lowEnd and $highEnd")
    return Bucket(
        id = this.id,
        bucketName = bucket_name,
        estimatedAmount = bucket_estimate.toFloat(),
        bucketType = bucket_type,
        transactions = budgyt.transactionQueries.getTransactionsForBucketForRange(
            this.id, lowEnd, highEnd
        ).executeAsList().map { budgetTransaction -> budgetTransaction.toApplicationDataModel() }
    )
}

fun Bucket.toApplicationDataModel(): models.Bucket {
    return Bucket(
        id = this.id,
        bucketName = bucket_name,
        estimatedAmount = bucket_estimate.toFloat(),
        bucketType = bucket_type,
        transactions = emptyList()
    )
}
