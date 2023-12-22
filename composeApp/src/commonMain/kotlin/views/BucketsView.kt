package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.BudgetCard
import models.BucketType
import viewmodels.ListComponent

@Composable
fun BucketsView(component: ListComponent) { //Really, this is a bucket of buckets.
    val bucketsState = component.model.subscribeAsState()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //This component should also hold the state for the inner internal items
        LazyColumn {
            items(bucketsState.value) { container ->
                Text(
                    text = when(container.containerType) {
                        BucketType.INFLOW -> "Inflow"
                        BucketType.OUTFLOW -> "Outflow"
                        BucketType.FUND -> "Fund"
                    },
                    fontSize = 32.sp,
                    modifier = Modifier.padding(8.dp)
                )
                if(container.buckets.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(0.8f)
                        .padding(16.dp).height(IntrinsicSize.Min), elevation = 4.dp) {
                        Text("No buckets for this category yet!", textAlign = TextAlign.Center, modifier = Modifier.padding(4.dp))
                    }
                } else {
                    container.buckets.forEach { bucket ->
                        BudgetCard(
                            name = bucket.value.bucketName,
                            estimatedAmount = 100f,
                            actualAmount = bucket.value.transactions.sumOf { it.transactionAmount.toDouble() },
                            onClick = fun() {
                                component.onItemClicked(bucket.value)
                            }
                        )
                    }
                }
            }
        }
        Button(onClick = { component.onAddTransactionButtonClicked() }, content = {
            Text(text= "Add New Transaction")
        })
    }
}