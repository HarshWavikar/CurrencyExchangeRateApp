package com.harshcode.currencyexchange.util

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun Modifier.wavingFlag(
    amplitude: Dp = 4.dp,
    frequency: Float = 0.02f,
    durationMillis: Int = 1800
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "flagWave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    return this.drawWithContent {
        val amplitudePx = amplitude.toPx()
        val sliceWidth = 8f // Increased slice width to be much more efficient and prevent Metal command buffer overflow/crashes on iOS

        val widthInt = size.width.toInt()
        val stepInt = sliceWidth.toInt()
        if (widthInt > 0 && stepInt > 0) {
            for (x in 0 until widthInt step stepInt) {
                val offsetX = x.toFloat()
                val offsetY = amplitudePx * sin(frequency * offsetX + phase)

                clipRect(
                    left = offsetX,
                    top = -amplitudePx, // Expand top/bottom to prevent clipping the wave itself
                    right = offsetX + sliceWidth,
                    bottom = size.height + amplitudePx
                ) {
                    drawContext.canvas.save()
                    drawContext.canvas.translate(0f, offsetY)
                    this@drawWithContent.drawContent()
                    drawContext.canvas.restore()
                }
            }
        } else {
            this@drawWithContent.drawContent()
        }
    }
}
