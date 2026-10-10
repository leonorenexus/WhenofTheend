package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OverdueScreen(vm: AppViewModel) {
    val s = vm.snapshot
    val ctx = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Column(Modifier.fillMaxSize().gridBackground().systemBarsPadding().padding(12.dp)) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Ink.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).background(Ink.Black), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = Ink.White, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "OVERDUE", color = Ink.Black, fontFamily = Ink.Sans,
                        fontWeight = FontWeight.Black, fontSize = 44.sp, letterSpacing = (-1).sp
                    )
                    Text(
                        "MISSED CHECK-IN", color = Ink.Black, fontFamily = Ink.Mono,
                        fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 2.sp
                    )
                }
            }

            Box(Modifier.fillMaxWidth().height(4.dp).background(Ink.Black))

            Label("TIME SINCE DEADLINE", color = Ink.Black.copy(alpha = 0.65f))
            Countdown(remainingMs = s.overdueMs, fontSize = 46.sp, color = Ink.Black, spoken = "since the deadline")
            BodyText(
                "DEADLINE WAS ${fmtDateTime(s.deadlineWall)}. LAST CHECK-IN ${fmtDateTime(s.lastCheckInWall)}.",
                color = Ink.Black
            )
            BodyText(
                "A MISSED CHECK-IN IS NOT PROOF OF AN EMERGENCY. THE PHONE MAY HAVE BEEN OFF, OUT OF REACH, OR THE CHECK-IN SIMPLY FORGOTTEN.",
                color = Ink.Black.copy(alpha = 0.8f)
            )

            BrutalButton(
                label = "I AM STILL HERE — CHECK IN",
                onClick = {
                    if (vm.settings.haptics) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    vm.checkIn()
                },
                modifier = Modifier.fillMaxWidth(),
                fill = Ink.Black, textColor = Ink.White, border = Ink.Black, shadow = Ink.Ash,
                fontSize = 20.sp, minHeight = 80.dp
            )

            Label("CONTACT A TRUSTED PERSON", color = Ink.Black.copy(alpha = 0.65f))
            if (vm.contacts.isEmpty()) {
                BodyText("NO TRUSTED CONTACTS CONFIGURED YET.", color = Ink.Black)
                BrutalButton(
                    label = "SET UP CONTACTS",
                    onClick = { vm.screen = Screen.CONTACTS },
                    modifier = Modifier.fillMaxWidth(),
                    fill = Ink.White, textColor = Ink.Black, border = Ink.Black, shadow = Ink.Ash,
                    fontSize = 16.sp
                )
            } else {
                vm.contacts.forEach { c ->
                    BrutalButton(
                        label = (if (c.pref == "SMS") "TEXT " else "CALL ") + c.name.uppercase(),
                        onClick = { launchContact(ctx, c) },
                        modifier = Modifier.fillMaxWidth(),
                        fill = Ink.White, textColor = Ink.Black, border = Ink.Black, shadow = Ink.Ash,
                        fontSize = 16.sp
                    )
                }
                BodyText("OPENS YOUR DIALER OR MESSAGES. NOTHING IS SENT UNTIL YOU PRESS CALL OR SEND.", color = Ink.Black.copy(alpha = 0.7f))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "HISTORY" to Screen.HISTORY,
                    "CONTACTS" to Screen.CONTACTS,
                    "SETTINGS" to Screen.SETTINGS
                ).forEach { (name, target) ->
                    BrutalButton(
                        label = name,
                        onClick = { vm.screen = target },
                        modifier = Modifier.weight(1f),
                        fill = Ink.White, textColor = Ink.Black, border = Ink.Black, shadow = Ink.Ash,
                        fontSize = 11.sp, minHeight = 48.dp
                    )
                }
            }
        }
    }
}
