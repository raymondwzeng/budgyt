package components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun DeletionConfirmationDialog(
    text: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "Heads Up!", fontSize = 24.sp)
                Text(text = text, modifier = Modifier.fillMaxHeight(0.7f), fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "No",
                        modifier = Modifier.clickable(onClick = onDismissRequest)
                            .padding(horizontal = 32.dp)
                    )
                    Text(text = "Yes", modifier = Modifier.clickable(onClick = onConfirm))
                }
            }
        }
    }
}