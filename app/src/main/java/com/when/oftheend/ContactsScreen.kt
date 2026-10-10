package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Ink.White,
    unfocusedTextColor = Ink.White,
    cursorColor = Ink.White,
    focusedBorderColor = Ink.White,
    unfocusedBorderColor = Ink.Ash,
    focusedLabelColor = Ink.White,
    unfocusedLabelColor = Ink.White.copy(alpha = 0.7f),
    focusedContainerColor = Ink.Deep,
    unfocusedContainerColor = Ink.Deep
)

@Composable
fun ContactsScreen(vm: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pref by remember { mutableStateOf("CALL") }
    var consent by remember { mutableStateOf(false) }
    val valid = name.isNotBlank() && phone.count { it.isDigit() } >= 3 && consent
    val full = vm.contacts.size >= 5

    ScreenFrame("TRUSTED CONTACTS", onBack = { vm.screen = Screen.HOME }) {
        BodyText("NOTHING IS SENT AUTOMATICALLY. IN THIS VERSION A CONTACT IS ONLY USED WHEN YOU TAP CALL OR TEXT ON THE OVERDUE SCREEN.")

        SectionTitle("EMERGENCY ACTIONS")
        BrutalSurface(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionLine(
                    "TAP-TO-CALL / TEXT FROM OVERDUE SCREEN",
                    if (vm.contacts.isEmpty()) "NOT CONFIGURED" else "CONFIGURED (${vm.contacts.size})"
                )
                ActionLine("AUTOMATIC MESSAGE TO CONTACTS", "NOT AVAILABLE — FUTURE")
                ActionLine("DEVICE FACTORY RESET / DATA WIPE", "NOT SUPPORTED — NEVER")
            }
        }

        SectionTitle("SAVED CONTACTS")
        if (vm.contacts.isEmpty()) {
            BodyText("NONE YET.")
        } else {
            vm.contacts.forEach { c ->
                BrutalSurface(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            c.name.uppercase(), color = Ink.White, fontFamily = Ink.Sans,
                            fontWeight = FontWeight.Black, fontSize = 20.sp
                        )
                        BodyText(c.phone, color = Ink.White)
                        Label("PREFERENCE: ${if (c.pref == "SMS") "TEXT (SMS)" else "PHONE CALL"}")
                        Label("CONSENT: ${if (c.consent) "GIVEN" else "NOT GIVEN"}")
                        Spacer(Modifier.height(4.dp))
                        BrutalButton(
                            label = "REMOVE", onClick = { vm.removeContact(c.id) },
                            modifier = Modifier.fillMaxWidth(), fill = Ink.Black, textColor = Ink.White,
                            shadow = Ink.Slate, fontSize = 14.sp, minHeight = 48.dp
                        )
                    }
                }
            }
        }

        SectionTitle("ADD CONTACT")
        if (full) {
            BodyText("MAXIMUM OF 5 CONTACTS. REMOVE ONE TO ADD ANOTHER.")
        } else {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.take(40) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("NAME", fontFamily = Ink.Mono) },
                singleLine = true,
                shape = RectangleShape,
                colors = fieldColors(),
                textStyle = TextStyle(fontFamily = Ink.Mono, fontSize = 16.sp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { v -> phone = v.filter { it.isDigit() || it == '+' || it == ' ' || it == '-' }.take(24) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("PHONE NUMBER", fontFamily = Ink.Mono) },
                singleLine = true,
                shape = RectangleShape,
                colors = fieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                textStyle = TextStyle(fontFamily = Ink.Mono, fontSize = 16.sp)
            )
            Label("COMMUNICATION PREFERENCE")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceChip("CALL", pref == "CALL", { pref = "CALL" }, Modifier.weight(1f))
                ChoiceChip("TEXT", pref == "SMS", { pref = "SMS" }, Modifier.weight(1f))
            }
            CheckRow(
                checked = consent,
                onChange = { consent = it },
                text = "I CONFIRM THIS PERSON AGREED TO BE MY TRUSTED CONTACT, AND I CONSENT TO THIS APP OPENING A CALL OR MESSAGE TO THEM WHEN I TAP IT."
            )
            BrutalButton(
                label = "SAVE TRUSTED CONTACT",
                onClick = {
                    vm.addContact(name.trim(), phone.trim(), pref, consent)
                    name = ""; phone = ""; pref = "CALL"; consent = false
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = valid
            )
            if (!valid) BodyText("REQUIRED: NAME, PHONE NUMBER, AND CONSENT.")
        }
    }
}

@Composable
private fun ActionLine(title: String, state: String) {
    Column {
        Label(title)
        Text(state, color = Ink.White, fontFamily = Ink.Mono, fontWeight = FontWeight.Black, fontSize = 14.sp)
    }
}
