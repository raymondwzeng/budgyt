import app.cash.sqldelight.db.SqlDriver
import com.technology626.budgyt.budgyt

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): budgyt {
    val driver = driverFactory.createDriver()
    val database = budgyt(driver)

    return database
}