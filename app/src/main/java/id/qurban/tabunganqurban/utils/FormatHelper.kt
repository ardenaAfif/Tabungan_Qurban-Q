package id.qurban.tabunganqurban.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    fun formatCurrencyString(input: String): String {
        val number = input.toLongOrNull() ?: 0L
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)

        return "Rp ${formatter.format(number.coerceAtLeast(0))}"
    }

    fun formatCurrencyDouble(amount: Double): String {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(amount)}"
    }

    fun formatDate(): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta") // Set ke WIB (GMT +7)
        return dateFormat.format(Date())
    }
}