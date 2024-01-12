import androidx.compose.ui.graphics.Color
import java.math.BigDecimal


val DARKER_GREEN = Color(0, 215, 57)

fun returnMonetaryValueColor(value: BigDecimal, defaultPositiveColor: Color = DARKER_GREEN): Color {
    return when {
        value < BigDecimal(0) -> Color.Red
        value > BigDecimal(0) -> defaultPositiveColor
        else -> Color.Gray
    }
}