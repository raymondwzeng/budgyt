package views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import models.BucketType
import viewmodels.EditBucketComponent
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EditBucketView(component: EditBucketComponent) {
    val bucketName = remember { mutableStateOf("") }
    val bucketType = remember { mutableStateOf(BucketType.INFLOW) }
    val bucketTypeExpanded = remember { mutableStateOf(false) }
    val bucketEstimateAmount = remember { mutableStateOf(0f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Bucket Name", fontSize = 24.sp)
        TextField(
            value = bucketName.value,
            onValueChange = { newValue -> bucketName.value = newValue }
        )
        Text(text = "Bucket Type", fontSize = 24.sp)
        Surface {
            TextField(
                value = bucketType.value.toString(),
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Button(onClick = {
                        bucketTypeExpanded.value = !bucketTypeExpanded.value
                    }) {
                        Text(">")
                    }
                }
            )
            DropdownMenu(
                expanded = bucketTypeExpanded.value,
                onDismissRequest = { bucketTypeExpanded.value = false }
            ) {
                BucketType.values().forEach { bucketTypeChoice ->
                    DropdownMenuItem(onClick = {
                        bucketType.value = bucketTypeChoice
                        bucketTypeExpanded.value = false
                    }) {
                        Text(text = bucketTypeChoice.name, fontSize = 16.sp)
                    }
                }
            }
        }
        Text(text = "Bucket Estimated Amount", fontSize = 24.sp)
        TextField(
            value = NumberFormat.getCurrencyInstance(Locale.US).format(bucketEstimateAmount.value),
            onValueChange = { newAmount -> bucketEstimateAmount.value = newAmount.substring(1).toFloat()},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            component.addBucket(bucketName = bucketName.value, bucketType = bucketType.value, bucketEstimate = bucketEstimateAmount.value)
        }) {
            Text(text = "Add New Bucket")
        }
    }
}