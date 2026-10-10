package com.`when`.oftheend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val st = vm.settings
    val haptic = LocalHapticFeedback.current

    ScreenFrame("SETTINGS", onBack = { vm.screen = Screen.HOME }) {
        SectionTitle("01 · CHECK-IN INTERVAL")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(12, 24, 48, 72).forEach { h ->
                ChoiceChip("${h}H", st.intervalHours == h, { vm.setInterval(h) }, Modifier.weight(1f))
            }
        }
        BodyText("DEFAULT IS 48H. THE DEADLINE IS ALWAYS LAST CHECK-IN + INTERVAL, SO CHANGING THIS UPDATES THE CURRENT DEADLINE IMMEDIATELY.")

        SectionTitle("02 · MOTION & FEEDBACK")
        ToggleRow(
            label = "REDUCE MOTION",
            detail = "STOPS THE HOURGLASS ROTATION AND SCREEN FADES. ALSO APPLIED AUTOMATICALLY WHEN ANDROID ANIMATIONS ARE TURNED OFF.",
            checked = st.reduceMotion,
            onChange = { vm.setReduceMotion(it) }
        )
        ToggleRow(
            label = "HAPTIC FEEDBACK",
            detail = "VIBRATION ON CHECK-IN, WHERE THE DEVICE SUPPORTS IT.",
            checked = st.haptics,
            onChange = {
                vm.setHaptics(it)
                if (it) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        )

        SectionTitle("03 · NOTIFICATIONS")
        BodyText("REMINDER NOTIFICATIONS: PLANNED, NOT IN THIS BUILD. THE APP CURRENTLY ONLY UPDATES WHILE IT IS OPEN. IT CANNOT ALERT YOU OR ANYONE ELSE IN THE BACKGROUND.")

        SectionTitle("04 · APPEARANCE")
        BodyText("MONOCHROME BLACK AND WHITE. FIXED BY DESIGN.")

        SectionTitle("05 · DATA & PRIVACY")
        BodyText("ALL DATA STAYS ON THIS DEVICE. THE APP REQUESTS NO PERMISSIONS: NO NETWORK, NO CONTACTS, NO SMS. IT CANNOT AND WILL NOT FACTORY-RESET THE PHONE OR TOUCH ANY FILE OUTSIDE ITS OWN STORAGE.")
        ConfirmButton("CLEAR CHECK-IN HISTORY") { vm.clearHistory() }
        ConfirmButton("ERASE ALL APP DATA") { vm.eraseAll() }
        BodyText("ERASING RESETS THIS APP ONLY (HISTORY, CONTACTS, SETTINGS) AND RESTARTS THE CHECK-IN WINDOW.")

        SectionTitle("06 · TIMING NOTES")
        BodyText("THE COUNTDOWN USES A MONOTONIC CLOCK WHILE THE PHONE STAYS ON, SO CHANGING THE SYSTEM CLOCK DOES NOT MOVE THE DEADLINE. AFTER A REBOOT IT FALLS BACK TO THE WALL CLOCK. NOTHING RUNS WHILE THE PHONE IS OFF; THE STATUS IS RECALCULATED WHEN THE APP OPENS.", modifier = Modifier.padding(bottom = 8.dp))
    }
}
