package com.harshcode.currencyexchange.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import currencyexchange.shared.generated.resources.Res
import currencyexchange.shared.generated.resources.bebas_neue_regular
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun calculateExchangeRate(source: Double, target: Double): Double {
    return target / source
}

fun convert(amount: Double, exchangeRate: Double): Double {
    return amount * exchangeRate
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

fun displayCurrentDateTime(): String {
    val currentTimestamp = Clock.System.now()
    val date = currentTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())

    // Format the LocalDate into the desired representation
    val dayOfMonth = date.day
    val month =
        date.month.toString().lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    val year = date.year

    // Determine the suffix for the day of the month
    val suffix = when {
        dayOfMonth in 11..13 -> "ᵗʰ" // Special case for 11th, 12th, and 13th
        dayOfMonth % 10 == 1 -> "ˢᵗ"
        dayOfMonth % 10 == 2 -> "ⁿᵈ"
        dayOfMonth % 10 == 3 -> "ʳᵈ"
        else -> "ᵗʰ"
    }

    // Format the date in the desired representation
    return "$dayOfMonth$suffix $month, $year."
}

@Composable
fun GetBebasFontFamily() = FontFamily(Font(Res.font.bebas_neue_regular))