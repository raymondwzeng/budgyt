import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.technology626.budgyt.budgyt

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        context.deleteDatabase("budgyt.db")
        return AndroidSqliteDriver(budgyt.Schema, context, "budgyt.db")
    }
}