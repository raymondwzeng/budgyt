import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import viewmodels.BudgetOverviewViewModel
import javax.swing.SwingUtilities
import createDatabase

internal fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }

    error?.also { throw it }

    @Suppress("UNCHECKED_CAST")
    return result as T
}

fun main() = application {
    val lifecycle = LifecycleRegistry()

    val root = runOnUiThread {
        BudgetOverviewViewModel (
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            database = createDatabase(DriverFactory())
        )
    }

    Window(onCloseRequest = ::exitApplication) {
        App(root)
    }
}