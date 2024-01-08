import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import viewmodels.BaseViewModel
import views.EditBucketView
import views.EditTransactionView
import views.ContainerView
import views.TransactionDetailView
import views.TransactionsView

@Composable
fun App(component: BaseViewModel) {
    val child = component.callstack.subscribeAsState()
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Budgyt", fontSize = 48.sp)
                when(child.value.active.instance) {
                    is BaseViewModel.Child.ListChild -> ContainerView(component = (child.value.active.instance as BaseViewModel.Child.ListChild).component)
                    is BaseViewModel.Child.BucketDetailsChild -> TransactionsView(component = (child.value.active.instance as BaseViewModel.Child.BucketDetailsChild).component)
                    is BaseViewModel.Child.AddTransactionChild -> EditTransactionView(component = (child.value.active.instance as BaseViewModel.Child.AddTransactionChild).component)
                    is BaseViewModel.Child.EditBucketChild -> EditBucketView(component = (child.value.active.instance as BaseViewModel.Child.EditBucketChild).component)
                    is BaseViewModel.Child.TransactionDetailsChild -> TransactionDetailView(component = (child.value.active.instance as BaseViewModel.Child.TransactionDetailsChild).component)
                }
            }
        }
    }
}