package com.`when`.oftheend

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

private class Pt(val x: Float, val y: Float, val d: Float)

private fun project(
    px: Float, py: Float, pz: Float,
    yaw: Float, tilt: Float,
    cx: Float, cy: Float, s: Float
): Pt {
    val c = cos(yaw)
    val sn = sin(yaw)
    val x = px * c + pz * sn
    val z = -px * sn + pz * c
    val ct = cos(tilt)
    val st = sin(tilt)
    val y = py * ct - z * st
    val z2 = py * st + z * ct
    val k = 1f / (1f - z2 * 0.10f)
    return Pt(cx + x * s * k, cy - y * s * k, z2)
}

/**
 * Procedural wireframe hourglass (lathe profile, rotating) with an orbit ring.
 * Bright rings = "sand": it moves from the upper to the lower bulb as the window elapses.
 */
@Composable
fun HourglassHero(
    elapsedFraction: Float,
    reduceMotion: Boolean,
    intervalHours: Int,
    modifier: Modifier = Modifier
) {
    val yaw: State<Float> = if (reduceMotion) {
        remember { mutableFloatStateOf(0.6f) }
    } else {
        val t = rememberInfiniteTransition(label = "hero")
        t.animateFloat(
            initialValue = 0f,
            targetValue = (2.0 * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
            label = "yaw"
        )
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Ink.Deep)
            .border(3.dp, Ink.White)
            .clipToBounds()
            .semantics { contentDescription = "Hourglass graphic showing how much of the check-in window has elapsed" }
    ) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 52.dp)
                .fillMaxWidth(0.55f)
                .height(190.dp)
                .glass()
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 56.dp)
                .fillMaxWidth(0.5f)
                .height(140.dp)
                .glass(strong = true)
        )

        Canvas(Modifier.fillMaxSize()) {
            val a = yaw.value
            val cx = size.width * 0.5f
            val cy = size.height * 0.52f
            val s = min(size.width, size.height) * 0.30f
            val tilt = 0.32f
            val hMax = 1.15f
            val thin = 1.dp.toPx()
            val mid = 1.6.dp.toPx()
            val thick = 3.2.dp.toPx()

            fun rad(y: Float): Float = 0.16f + 0.84f * (abs(y) / hMax).pow(1.7f)

            fun seg(p: Pt, q: Pt, base: Float, w: Float) {
                val al = (base * (0.45f + 0.2f * (p.d + q.d))).coerceIn(0.08f, 1f)
                drawLine(
                    color = Color.White.copy(alpha = al),
                    start = Offset(p.x, p.y),
                    end = Offset(q.x, q.y),
                    strokeWidth = w
                )
            }

            // floor shadow
            val g = project(0f, -hMax - 0.25f, 0f, a, tilt, cx, cy, s)
            drawOval(
                color = Color.White.copy(alpha = 0.07f),
                topLeft = Offset(g.x - s * 1.1f, g.y - s * 0.17f),
                size = Size(s * 2.2f, s * 0.34f)
            )

            // meridians
            val meridians = 12
            val steps = 24
            for (k in 0 until meridians) {
                val th = k * (2.0 * PI).toFloat() / meridians
                var prev = project(rad(-hMax) * cos(th), -hMax, rad(-hMax) * sin(th), a, tilt, cx, cy, s)
                for (j in 1..steps) {
                    val y = -hMax + j * (2f * hMax / steps)
                    val r = rad(y)
                    val cur = project(r * cos(th), y, r * sin(th), a, tilt, cx, cy, s)
                    seg(prev, cur, 0.8f, thin)
                    prev = cur
                }
            }

            // rings (sand level decides which are bright)
            val lowerLevel = -hMax + elapsedFraction * hMax
            val upperLevel = (1f - elapsedFraction) * hMax
            val rings = 10
            val ringSteps = 36
            for (i in 0..rings) {
                val y = -hMax + i * (2f * hMax / rings)
                val r = rad(y)
                val sand = (y < 0f && y <= lowerLevel) || (y >= 0f && y <= upperLevel)
                val cap = i == 0 || i == rings
                val w = if (cap) thick else if (sand) mid * 1.4f else thin
                val base = if (sand || cap) 1f else 0.7f
                var prev = project(r, y, 0f, a, tilt, cx, cy, s)
                for (j in 1..ringSteps) {
                    val th = j * (2.0 * PI).toFloat() / ringSteps
                    val cur = project(r * cos(th), y, r * sin(th), a, tilt, cx, cy, s)
                    seg(prev, cur, base, w)
                    prev = cur
                }
            }

            // falling stream
            if (elapsedFraction < 0.999f) {
                val top = project(0f, 0f, 0f, a, tilt, cx, cy, s)
                val bottom = project(0f, lowerLevel, 0f, a, tilt, cx, cy, s)
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(top.x, top.y),
                    end = Offset(bottom.x, bottom.y),
                    strokeWidth = thin * 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f))
                )
            }

            // orbit ring + moon
            val orbitR = 1.55f
            val ot = 0.5f
            val os = 0.4f
            var prevO = project(orbitR, 0f, 0f, os, tilt, cx, cy, s)
            for (j in 1..48) {
                val th = j * (2.0 * PI).toFloat() / 48
                val cur = project(
                    orbitR * cos(th), orbitR * sin(th) * sin(ot), orbitR * sin(th) * cos(ot),
                    os, tilt, cx, cy, s
                )
                seg(prevO, cur, 0.9f, thin)
                prevO = cur
            }
            val mth = a * 4f
            val m = project(
                orbitR * cos(mth), orbitR * sin(mth) * sin(ot), orbitR * sin(mth) * cos(ot),
                os, tilt, cx, cy, s
            )
            drawCircle(Color.White, radius = 5.dp.toPx(), center = Offset(m.x, m.y))
        }

        Tag(
            "CONTINUITY PROTOCOL",
            filled = true,
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
        )
        Tag(
            "$intervalHours-HOUR CHECK-IN SYSTEM",
            filled = false,
            modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
        )
    }
}
