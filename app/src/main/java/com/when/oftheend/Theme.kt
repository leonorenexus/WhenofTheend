package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

object Ink {
    val Black = Color(0xFF000000)
    val Deep = Color(0xFF0A0A0A)
    val Charcoal = Color(0xFF171717)
    val Slate = Color(0xFF2E2E2E)
    val White = Color(0xFFFFFFFF)
    val Soft = Color(0xFFF5F5F5)
    val Ash = Color(0xFF6E6E6E)
    val GlassBorder = Color(0x38FFFFFF)
    val Mono: FontFamily = FontFamily.Monospace
    val Sans: FontFamily = FontFamily.SansSerif
}

@Composable
fun WhenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Ink.White,
            onPrimary = Ink.Black,
            background = Ink.Black,
            onBackground = Ink.White,
            surface = Ink.Charcoal,
            onSurface = Ink.White
        ),
        content = content
    )
}

/** Black background with a faint static grid (drawn once per size, no animation). */
fun Modifier.gridBackground(): Modifier = this.background(Ink.Black).drawBehind {
    val step = 32.dp.toPx()
    val c = Color.White.copy(alpha = 0.045f)
    var x = 0f
    while (x < size.width) {
        drawLine(c, Offset(x, 0f), Offset(x, size.height), 1f)
        x += step
    }
    var y = 0f
    while (y < size.height) {
        drawLine(c, Offset(0f, y), Offset(size.width, y), 1f)
        y += step
    }
}

/** Translucent panel: white 8-14% fill, thin white border. (No backdrop blur: Compose has none without extra libs.) */
fun Modifier.glass(strong: Boolean = false): Modifier = this
    .background(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (strong) 0.14f else 0.10f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    )
    .border(1.dp, Ink.GlassBorder)
