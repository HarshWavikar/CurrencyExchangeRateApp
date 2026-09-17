package com.harshcode.currencyexchange.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
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

fun Double.toCleanString(decimals: Int): String {
    val s = this.toString()
    if (!s.contains('E', ignoreCase = true)) {
        val parts = s.split(".")
        if (parts.size == 2) {
            val fractional = parts[1]
            if (fractional.length > decimals) {
                return "${parts[0]}.${fractional.substring(0, decimals)}"
            } else {
                return "${parts[0]}.${fractional.padEnd(decimals, '0')}"
            }
        }
        return s
    }
    
    val parts = s.split(Regex("[eE]"))
    val baseSignificand = parts[0]
    val exponent = parts[1].toInt()
    
    val dotIndex = baseSignificand.indexOf('.')
    val significand = baseSignificand.replace(".", "")
    
    val adjustedExponent = exponent + (if (dotIndex >= 0) dotIndex - 1 else significand.length - 1)
    
    val result = when {
        adjustedExponent >= 0 -> {
            val sb = StringBuilder(significand)
            while (sb.length <= adjustedExponent) sb.append('0')
            if (sb.length > adjustedExponent + 1) sb.insert(adjustedExponent + 1, '.')
            sb.toString()
        }
        else -> {
            val sb = StringBuilder("0.")
            repeat(-adjustedExponent - 1) { sb.append('0') }
            sb.append(significand)
            sb.toString()
        }
    }
    
    val resParts = result.split(".")
    if (resParts.size == 2) {
        val fractional = resParts[1]
        if (fractional.length > decimals) {
            return "${resParts[0]}.${fractional.substring(0, decimals)}"
        } else {
            return "${resParts[0]}.${fractional.padEnd(decimals, '0')}"
        }
    }
    return result
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

@Composable
fun ExchangeRateText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        fontSize = MaterialTheme.typography.bodyLargeEmphasized.fontSize,
        fontWeight = FontWeight.Bold,
        color = if (isSystemInDarkTheme())
            Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        lineHeight = 20.sp
    )
}