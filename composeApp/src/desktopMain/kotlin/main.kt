import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import createDatabase
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import viewmodels.BudgetOverviewViewModel
import javax.swing.SwingUtilities

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

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    val lifecycle = LifecycleRegistry()

    val root = runOnUiThread {
        BudgetOverviewViewModel (
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            database = createDatabase(DriverFactory())
        )
    }

    Window(
        title = "Budgyt Desktop",
        icon = painterResource("logo.png"),
        onCloseRequest = ::exitApplication
    ) {
        App(DeviceType.DESKTOP, root)
    }
}