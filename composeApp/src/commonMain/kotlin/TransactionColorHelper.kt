import androidx.compose.ui.graphics.Color
import java.math.BigDecimal

fun returnMonetaryValueColor(value: BigDecimal): Color {
    return when {
        value < BigDecimal(0) -> Color.Red
        value > BigDecimal(0) -> Color(0, 215, 57)
        else -> Color.Gray
    }
}