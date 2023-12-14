package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import models.Transaction
import viewmodels.DetailsComponent

@Composable
fun TransactionsView(component: DetailsComponent) {
    val bucketState = component.model.subscribeAsState()

    Column {
        Text(text = bucketState.value.bucketName)
        LazyColumn {
            items(bucketState.value.transactions) {transaction: Transaction ->
                //TODO: Actual transaction composable component
                Text(text = transaction.note)
            }
        }
    }
}