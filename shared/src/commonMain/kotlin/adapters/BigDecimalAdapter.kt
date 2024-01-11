package adapters

import app.cash.sqldelight.ColumnAdapter
import java.math.BigDecimal

val BigDecimalAdapter = object : ColumnAdapter<BigDecimal, String> {
    override fun decode(databaseValue: String): BigDecimal {
        return BigDecimal(databaseValue)
    }

    override fun encode(value: BigDecimal): String {
        return value.toString()
    }

}