package adapters

import app.cash.sqldelight.ColumnAdapter
import java.math.BigDecimal

val BigDecimalAdapter = object : ColumnAdapter<BigDecimal, Double> {
    override fun decode(databaseValue: Double): BigDecimal {
        return BigDecimal(databaseValue)
    }

    override fun encode(value: BigDecimal): Double {
        return BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }

}