package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.DeletionConfirmationDialog
import components.TransactionItem
import models.Transaction
import viewmodels.DetailsComponent

const val BUCKET_DELETION_DIALOG = "Are you sure that you want to remove this bucket? All associated transactions will also be removed!"
@Composable
fun BucketView(component: DetailsComponent) {
    val bucketState = component.bucketModel.subscribeAsState()
    val deletionConfirmationState = remember { mutableStateOf(false) }

    if (deletionConfirmationState.value) {
        DeletionConfirmationDialog(text = BUCKET_DELETION_DIALOG, onConfirm = {
            component.removeBucket()
            deletionConfirmationState.value = false
        }, onDismissRequest = {
            deletionConfirmationState.value = false
        })
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = bucketState.value.bucketName, fontSize = 32.sp)
        LazyColumn {
            items(bucketState.value.transactions) { transaction: Transaction ->
                TransactionItem(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    transaction = transaction,
                    onClick = {
                        component.navigateToTransactionDetail(transaction)
                    }
                )
            }
        }
        Button({ component.navigateToEditBucket() }) {
            Text("Edit Bucket")
        }
        Button({ deletionConfirmationState.value = !deletionConfirmationState.value }) {
            Text("Delete Bucket")
        }
    }
}