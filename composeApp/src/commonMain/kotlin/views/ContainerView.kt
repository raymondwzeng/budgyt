package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import components.BucketCard
import components.InflowOutflowComponent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import models.BucketType
import viewmodels.ListComponent

val months = mapOf(
    1 to "January",
    2 to "February",
    3 to "March",
    4 to "April",
    5 to "May",
    6 to "June",
    7 to "July",
    8 to "August",
    9 to "September",
    10 to "October",
    11 to "November",
    12 to "December"
)

val years = (2023..Clock.System.todayIn(TimeZone.currentSystemDefault()).year).toList()

@Composable
fun ContainerView(component: ListComponent) { //Really, this is a bucket of buckets.
    val bucketsState = component.model.subscribeAsState()
    val dateState = component.currentDate.subscribeAsState()
    val changeMonthDropdown = remember { mutableStateOf(false) }
    val changeYearDropdown = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Card(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp)
                ) {
                    Text(
                        text = dateState.value.month.name,
                        fontSize = 24.sp
                    )
                    IconButton(
                        onClick = {
                            changeMonthDropdown.value = !changeMonthDropdown.value
                        }
                    ) {
                        if (changeMonthDropdown.value) {
                            Icon(
                                Icons.Default.ExpandLess,
                                contentDescription = "Collapse Change Month Dropdown"
                            )
                        } else {
                            Icon(
                                Icons.Default.ExpandMore,
                                contentDescription = "Expand Change Month Dropdown"
                            )
                        }
                    }
                }
                if (changeMonthDropdown.value)
                    Surface {
                        DropdownMenu(
                            expanded = changeMonthDropdown.value,
                            onDismissRequest = {
                                changeMonthDropdown.value = !changeMonthDropdown.value
                            }) {
                            months.forEach { (monthIndex, monthName) ->
                                DropdownMenuItem(onClick = {
                                    component.updateCurrentDate(monthIndex, dateState.value.year)
                                    changeMonthDropdown.value = false
                                }) {
                                    Text(text = monthName)
                                }
                            }
                        }
                    }
            }

            Card(modifier = Modifier.padding(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 8.dp)
                ) {
                    Text(
                        text = dateState.value.year.toString(),
                        fontSize = 24.sp
                    )
                    IconButton(
                        onClick = {
                            changeYearDropdown.value = !changeYearDropdown.value
                        }
                    ) {
                        if (changeYearDropdown.value) {
                            Icon(
                                Icons.Default.ExpandLess,
                                contentDescription = "Collapse Change Year Dropdown"
                            )
                        } else {
                            Icon(
                                Icons.Default.ExpandMore,
                                contentDescription = "Expand Change Year Dropdown"
                            )
                        }
                    }
                }
                if (changeYearDropdown.value)
                    Surface {
                        DropdownMenu(
                            expanded = changeYearDropdown.value,
                            onDismissRequest = {
                                changeYearDropdown.value = !changeYearDropdown.value
                            }) {
                            years.forEach { year ->
                                DropdownMenuItem(onClick = {
                                    component.updateCurrentDate(dateState.value.monthNumber, year)
                                    changeYearDropdown.value = false
                                }) {
                                    Text(text = year.toString())
                                }
                            }
                        }
                    }
            }
        }
        InflowOutflowComponent(
            inflowContainers = bucketsState.value.filter { container -> container.containerType == BucketType.INFLOW },
            outflowContainers = bucketsState.value.filter { container -> container.containerType == BucketType.OUTFLOW },
            fundContainers = bucketsState.value.filter { container -> container.containerType == BucketType.FUND },
        )
        if (bucketsState.value.isEmpty()) {
            Text(text = "No transactions were made within this month.")
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //This component should also hold the state for the inner internal items
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(bucketsState.value) { container ->
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
                                actualAmount = bucket.value.transactions.sumOf { it.transactionAmount },
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
}