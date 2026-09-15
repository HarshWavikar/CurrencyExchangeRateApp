package com.harshcode.currencyexchange.domain.model

import androidx.compose.ui.graphics.Color
import com.harshcode.currencyexchange.ui.freshColor
import com.harshcode.currencyexchange.ui.staleColor

enum class RateStatus(
    val title: String,
    val color: Color
) {
    Idle(title = "Rates", color = Color.White),
    Fresh(title = "Rates are fresh", color = freshColor),
    Stale(title = "Rates are not fresh", color = staleColor)
}