package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun statusIcon(s: CheckInStatus): ImageVector = when (s) {
    CheckInStatus.ACTIVE -> Icons.Filled.CheckCircle
    CheckInStatus.REQUIRED -> Icons.Filled.Warning
    CheckInStatus.OVERDUE -> Icons.Filled.Close
}

@Composable
fun HomeScreen(vm: AppViewModel) {
    val s = vm.snapshot
    val haptic = LocalHapticFeedback.current

    Column(Modifier.fillMaxSize().gridBackground().systemBarsPadding()) {
        HomeHeader(s.status, onSettings = { vm.screen = Screen.SETTINGS })
        Box(Modifier.fillMaxWidth().height(4.dp).background(Ink.White))

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (vm.recovered) {
                BrutalSurface(Modifier.fillMaxWidth(), fill = Ink.White, shadow = Ink.Ash) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            "LOCAL DATA WAS UNREADABLE AND HAS BEEN RESET. THE CHECK-IN WINDOW RESTARTED NOW.",
                            color = Ink.Black, fontFamily = Ink.Mono, fontWeight = FontWeight.Bold, fontSize = 12.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        BrutalButton(
                            label = "DISMISS", onClick = { vm.dismissRecovered() },
                            modifier = Modifier.fillMaxWidth(), fill = Ink.Black, textColor = Ink.White,
                            border = Ink.Black, shadow = Ink.Ash, fontSize = 14.sp, minHeight = 48.dp
                        )
                    }
                }
            }

            HourglassHero(
                elapsedFraction = s.elapsedFraction,
                reduceMotion = vm.motionReduced(),
                intervalHours = vm.settings.intervalHours,
                modifier = Modifier.padding(end = 0.dp)
            )

            Countdown(remainingMs = s.remainingMs)

            StatusCard(s)

            BrutalButton(
                label = "I AM STILL HERE",
                onClick = {
                    if (vm.settings.haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vm.checkIn()
                },
                modifier = Modifier.fillMaxWidth(),
                fontSize = 30.sp,
                minHeight = 96.dp
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NavButton("01  CHECK-IN HISTORY  →") { vm.screen = Screen.HISTORY }
                NavButton("02  TRUSTED CONTACTS  →") { vm.screen = Screen.CONTACTS }
                NavButton("03  PRIVACY & SETTINGS  →") { vm.screen = Screen.SETTINGS }
            }

            BodyText(
                "A MISSED CHECK-IN IS ONLY A MISSED CHECK-IN. THIS APP NEVER ASSUMES ANYTHING ELSE. NO DATA LEAVES THIS DEVICE.",
                color = Ink.White.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun NavButton(label: String, onClick: () -> Unit) {
    BrutalButton(
        label = label,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        fill = Ink.Charcoal,
        textColor = Ink.White,
        shadow = Ink.Slate,
        fontSize = 15.sp,
        alignStart = true
    )
}

@Composable
private fun HomeHeader(status: CheckInStatus, onSettings: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "WHEN OF\nTHE END",
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = "When of the end" },
            color = Ink.White,
            fontFamily = Ink.Sans,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            lineHeight = 30.sp,
            letterSpacing = (-1).sp
        )
        Row(
            Modifier
                .background(if (status == CheckInStatus.ACTIVE) Ink.Black else Ink.White)
                .border(2.dp, Ink.White)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fg = if (status == CheckInStatus.ACTIVE) Ink.White else Ink.Black
            Icon(statusIcon(status), contentDescription = null, tint = fg, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                status.short, color = fg, fontFamily = Ink.Mono,
                fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .size(48.dp)
                .background(Ink.Black)
                .border(3.dp, Ink.White)
                .clickable(role = Role.Button, onClick = onSettings),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Ink.White)
        }
    }
}

/** HH:MM:SS with labels. */
@Composable
fun Countdown(
    remainingMs: Long,
    fontSize: TextUnit = 60.sp,
    color: Color = Ink.White,
    spoken: String = "remaining"
) {
    val total = remainingMs / 1000L
    val h = total / 3600L
    val m = (total % 3600L) / 60L
    val s = total % 60L
    Row(
        Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "$h hours, $m minutes, $s seconds $spoken"
            },
        horizontalArrangement = Arrangement.Center
    ) {
        TimeCell(two(h), "HOURS", fontSize, color)
        TimeSep(fontSize, color)
        TimeCell(two(m), "MINUTES", fontSize, color)
        TimeSep(fontSize, color)
        TimeCell(two(s), "SECONDS", fontSize, color)
    }
}

@Composable
private fun TimeCell(value: String, label: String, fontSize: TextUnit, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontFamily = Ink.Mono,
            fontWeight = FontWeight.Black,
            fontSize = fontSize
        )
        Label(label, color = color.copy(alpha = 0.65f))
    }
}

@Composable
private fun TimeSep(fontSize: TextUnit, color: Color) {
    Text(
        text = ":",
        modifier = Modifier.padding(horizontal = 2.dp),
        color = color.copy(alpha = 0.5f),
        fontFamily = Ink.Mono,
        fontWeight = FontWeight.Black,
        fontSize = fontSize
    )
}

@Composable
private fun StatusCard(s: Snapshot) {
    BrutalSurface(Modifier.fillMaxWidth(), fill = Ink.Charcoal) {
        Column {
            StatusRow("STATE") {
                Icon(statusIcon(s.status), contentDescription = null, tint = Ink.White, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    s.status.label, color = Ink.White, fontFamily = Ink.Mono,
                    fontWeight = FontWeight.Black, fontSize = 14.sp
                )
            }
            Box(Modifier.fillMaxWidth().height(2.dp).background(Ink.White.copy(alpha = 0.35f)))
            StatusRow("LAST CHECK-IN") { ValueText(fmtDateTime(s.lastCheckInWall)) }
            Box(Modifier.fillMaxWidth().height(2.dp).background(Ink.White.copy(alpha = 0.35f)))
            StatusRow("DEADLINE") { ValueText(fmtDateTime(s.deadlineWall)) }
        }
    }
}

@Composable
private fun ValueText(text: String) {
    Text(text, color = Ink.White, fontFamily = Ink.Mono, fontWeight = FontWeight.Black, fontSize = 14.sp)
}

@Composable
private fun StatusRow(label: String, content: @Composable RowScope.() -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(label)
        Row(verticalAlignment = Alignment.CenterVertically, content = content)
    }
}
