import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import viewmodels.BaseViewModel
import views.AddBucketView
import views.AddTransactionView
import views.BucketsView
import views.TransactionsView

@Composable
fun App(component: BaseViewModel) {
    val child = component.callstack.subscribeAsState()
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when(child.value.active.instance) {
                is BaseViewModel.Child.ListChild -> BucketsView(component = (child.value.active.instance as BaseViewModel.Child.ListChild).component)
                is BaseViewModel.Child.DetailsChild -> TransactionsView(component = (child.value.active.instance as BaseViewModel.Child.DetailsChild).component)
                is BaseViewModel.Child.AddTransactionChild -> AddTransactionView(component = (child.value.active.instance as BaseViewModel.Child.AddTransactionChild).component)
                is BaseViewModel.Child.AddBucketChild -> AddBucketView(component = (child.value.active.instance as BaseViewModel.Child.AddBucketChild).component)
            }
        }
    }
}