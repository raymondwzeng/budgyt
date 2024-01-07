package views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.BucketCard
import kotlinx.coroutines.flow.collect
import models.BucketType
import viewmodels.ListComponent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContainerView(component: ListComponent) { //Really, this is a bucket of buckets.
    val bucketsState = component.model.subscribeAsState()
    val dateState = component.currentDate.subscribeAsState()
    val oldPageNumber = remember { mutableStateOf(1) }
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            component.changeMonth(oldPageNumber.value, page)
            oldPageNumber.value = page
        }
    }
    Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Current Month: ${dateState.value}")
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxHeight(0.8f)) { pageNumber ->
            val currentContainer = bucketsState.value[pageNumber]
            if(currentContainer.isEmpty()) {
                Text(text = "No transactions were made within this month.")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //This component should also hold the state for the inner internal items
                LazyColumn {
                    items(currentContainer) { container ->
                        Text(
                            text = when (container.containerType) {
                                BucketType.INFLOW -> "Inflow"
                                BucketType.OUTFLOW -> "Outflow"
                                BucketType.FUND -> "Fund"
                            },
                            fontSize = 32.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                        if (container.buckets.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(0.8f)
                                    .padding(16.dp).height(IntrinsicSize.Min), elevation = 4.dp
                            ) {
                                Text(
                                    "No buckets for this category yet!",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        } else {
                            container.buckets.forEach { bucket ->
                                BucketCard(
                                    name = bucket.value.bucketName,
                                    estimatedAmount = bucket.value.estimatedAmount,
                                    actualAmount = bucket.value.transactions.sumOf { it.transactionAmount.toDouble() },
                                    onClick = fun() {
                                        component.onItemClicked(bucket.value)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = { component.onAddTransactionButtonClicked() }, content = {
            Text(text = "Add New Transaction")
        })
        Button(onClick = { component.navigateToAddBucketSelected() }, content = {
            Text(text = "Add New Bucket")
        })
    }
}