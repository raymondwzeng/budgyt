import adapters.LocalDateAdapter
import adapters.UUIDAdapter
import adapters.BigDecimalAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.technology626.budgyt.Bucket
import com.technology626.budgyt.BudgetTransaction
import com.technology626.budgyt.budgyt

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): budgyt {
    val driver = driverFactory.createDriver()
    val database = budgyt(
        driver, BucketAdapter = Bucket.Adapter(
            idAdapter = UUIDAdapter,
            bucket_typeAdapter = EnumColumnAdapter(),
            bucket_estimateAdapter = BigDecimalAdapter
        ), BudgetTransactionAdapter = BudgetTransaction.Adapter(
            idAdapter = UUIDAdapter,
            bucket_idAdapter = UUIDAdapter,
            transaction_dateAdapter = LocalDateAdapter,
            transaction_amountAdapter = BigDecimalAdapter
        )
    )

    return database
}