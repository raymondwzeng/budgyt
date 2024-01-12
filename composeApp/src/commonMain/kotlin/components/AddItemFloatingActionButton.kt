package components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddItemFloatingActionButton(onAddBucket: () -> Unit, onAddTransaction: () -> Unit) {
    val expandAddButton = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = Modifier.fillMaxSize().let { modifier ->
        if (expandAddButton.value) {
            modifier.background(Color(100, 100, 100, 40))
                .clickable(interactionSource = interactionSource, indication = null) {
                    expandAddButton.value = false
                }
        } else {
            modifier
        }
    }, propagateMinConstraints = true, contentAlignment = Alignment.BottomEnd) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Bottom),
            modifier = Modifier.padding(16.dp)
        ) {
            if (expandAddButton.value) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bucket", modifier = Modifier.padding(end = 8.dp))
                    SmallFloatingActionButton(onClick = onAddBucket) {
                        Icon(Icons.Filled.Inventory2, contentDescription = "Add Bucket")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Transaction", modifier = Modifier.padding(end = 8.dp))
                    FloatingActionButton(onClick = onAddTransaction) {
                        Icon(Icons.Filled.ReceiptLong, contentDescription = "Add Transaction")
                    }
                }
            } else {
                FloatingActionButton(onClick = { expandAddButton.value = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Item")
                }
            }

        }
    }
}