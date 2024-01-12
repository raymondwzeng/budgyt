
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import viewmodels.BaseViewModel
import views.BucketView
import views.ContainerView
import views.EditBucketView
import views.EditTransactionView
import views.TransactionDetailView

enum class DeviceType {
    ANDROID,
    DESKTOP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(deviceType: DeviceType, component: BaseViewModel) {
    val child = component.callstack.subscribeAsState()
    MaterialTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colors.primarySurface,
                        titleContentColor = MaterialTheme.colors.onPrimary
                    ),
                    title = { //TODO: Make this dynamic
                        Text(text = "Budgyt")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            component.onBackClicked(child.value.items.lastIndex - 1)
                        }, enabled = child.value.items.size > 1) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (child.value.items.size > 1) MaterialTheme.colors.onPrimary else Color.LightGray
                            )
                        }
                    }
                )
            } //TODO: Floating action bar
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (child.value.active.instance) {
                    is BaseViewModel.Child.ListChild -> ContainerView(component = (child.value.active.instance as BaseViewModel.Child.ListChild).component)
                    is BaseViewModel.Child.BucketDetailsChild -> BucketView(component = (child.value.active.instance as BaseViewModel.Child.BucketDetailsChild).component)
                    is BaseViewModel.Child.AddTransactionChild -> EditTransactionView(component = (child.value.active.instance as BaseViewModel.Child.AddTransactionChild).component)
                    is BaseViewModel.Child.EditBucketChild -> EditBucketView(component = (child.value.active.instance as BaseViewModel.Child.EditBucketChild).component)
                    is BaseViewModel.Child.TransactionDetailsChild -> TransactionDetailView(
                        component = (child.value.active.instance as BaseViewModel.Child.TransactionDetailsChild).component
                    )
                }
            }
        }
    }
}