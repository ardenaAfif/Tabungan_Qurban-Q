package id.qurban.tabunganqurban.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object FormatHelper {

    /**
     * Convert a string to CamelCase format.
     * Each word starts with an uppercase letter, and other letters are lowercase.
     *
     * @return The formatted string in CamelCase.
     */
    fun String.toCamelCase(): String {
        return this.lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
    }

    fun formatCurrency(input: String): String {
        val number = input.toLongOrNull() ?: 0L
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)

        return "Rp ${formatter.format(number.coerceAtLeast(0))}"
    }
}