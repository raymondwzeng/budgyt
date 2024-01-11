package components

import GLOBAL_FORMATTER
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.math.abs
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun TransactionInputComponent(modifier: Modifier = Modifier, value: BigDecimal, onInputChange: (newValue: BigDecimal) -> Unit) {
    val formatted = GLOBAL_FORMATTER.format(value)
    TextField(
        value = TextFieldValue(
            text = formatted,
            selection = TextRange(formatted.length)
        ),
        modifier = modifier,
        singleLine = true,
        onValueChange = { newAmount ->
            var updatedValue = value
            if(newAmount.text.length - newAmount.text.indexOf('.') <= 2) {
                updatedValue = updatedValue.movePointLeft(1) //We need to force the scale to be 2?
                updatedValue = updatedValue.setScale(2, RoundingMode.DOWN)
            } else {
                val lastCharacter = newAmount.text[newAmount.text.length - 1]
                if(lastCharacter.isDigit()) {
                    updatedValue = updatedValue.movePointRight(1) //No need to update scale when moving right.
                    val newValue = BigDecimal("0.0$lastCharacter") //TODO: Is this jank?
                    updatedValue += newValue
                }
            }
            onInputChange(updatedValue)
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}