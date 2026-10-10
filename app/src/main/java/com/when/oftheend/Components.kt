package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Hard-edged card with an offset block shadow. */
@Composable
fun BrutalSurface(
    modifier: Modifier = Modifier,
    fill: Color = Ink.Charcoal,
    border: Color = Ink.White,
    shadow: Color? = Ink.Slate,
    depth: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier.padding(end = depth, bottom = depth)) {
        if (shadow != null) {
            Box(Modifier.matchParentSize().offset(depth, depth).background(shadow))
        }
        Box(Modifier.fillMaxWidth().background(fill).border(3.dp, border), content = content)
    }
}

/** Neo-brutalist button: thick border, hard shadow, translates into its shadow when pressed. */
@Composable
fun BrutalButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fill: Color = Ink.White,
    textColor: Color = Ink.Black,
    border: Color = Ink.White,
    shadow: Color = Ink.Ash,
    fontSize: TextUnit = 18.sp,
    minHeight: Dp = 56.dp,
    alignStart: Boolean = false,
    enabled: Boolean = true
) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val depth = 6.dp
    val shift = if (pressed) depth else 0.dp
    Box(modifier.padding(end = depth, bottom = depth).alpha(if (enabled) 1f else 0.4f)) {
        Box(Modifier.matchParentSize().offset(depth, depth).background(shadow))
        Box(
            Modifier
                .offset(shift, shift)
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .background(fill)
                .border(3.dp, border)
                .clickable(
                    interactionSource = src,
                    indication = null,
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onClick
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = if (alignStart) Alignment.CenterStart else Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontFamily = Ink.Sans,
                fontWeight = FontWeight.Black,
                fontSize = fontSize,
                letterSpacing = 1.sp,
                textAlign = if (alignStart) TextAlign.Start else TextAlign.Center
            )
        }
    }
}

@Composable
fun Label(text: String, modifier: Modifier = Modifier, color: Color = Ink.White.copy(alpha = 0.7f)) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = Ink.Mono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
}

@Composable
fun Tag(text: String, filled: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(if (filled) Ink.White else Ink.Black)
            .border(2.dp, Ink.White)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = if (filled) Ink.Black else Ink.White,
        fontFamily = Ink.Mono,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
fun SectionTitle(text: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = Ink.White,
            fontFamily = Ink.Mono,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(2.dp).background(Ink.White))
    }
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier, color: Color = Ink.White.copy(alpha = 0.8f)) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = Ink.Mono,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}

@Composable
fun ScreenFrame(
    title: String,
    onBack: () -> Unit,
    scroll: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize().gridBackground().systemBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .heightIn(min = 48.dp)
                    .background(Ink.Black)
                    .border(3.dp, Ink.White)
                    .clickable(role = Role.Button, onClick = onBack)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "← BACK",
                    color = Ink.White,
                    fontFamily = Ink.Mono,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.weight(1f))
            Label("WHEN OF THE END")
        }
        Box(Modifier.fillMaxWidth().height(4.dp).background(Ink.White))
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            color = Ink.White,
            fontFamily = Ink.Sans,
            fontWeight = FontWeight.Black,
            fontSize = 36.sp,
            letterSpacing = (-1).sp
        )
        if (scroll) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) { content() }
        } else {
            Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .heightIn(min = 52.dp)
            .background(if (selected) Ink.White else Ink.Charcoal)
            .border(3.dp, Ink.White)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Ink.Black else Ink.White,
            fontFamily = Ink.Mono,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ToggleRow(label: String, detail: String?, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Ink.Charcoal)
            .border(3.dp, Ink.White)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                label,
                color = Ink.White,
                fontFamily = Ink.Sans,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            if (detail != null) {
                Spacer(Modifier.height(4.dp))
                BodyText(detail)
            }
        }
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .heightIn(min = 40.dp)
                .width(64.dp)
                .background(if (checked) Ink.White else Ink.Black)
                .border(3.dp, Ink.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (checked) "ON" else "OFF",
                color = if (checked) Ink.Black else Ink.White,
                fontFamily = Ink.Mono,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CheckRow(checked: Boolean, onChange: (Boolean) -> Unit, text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onChange)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(28.dp)
                .background(if (checked) Ink.White else Ink.Deep)
                .border(3.dp, Ink.White),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text("X", color = Ink.Black, fontFamily = Ink.Mono, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
        Spacer(Modifier.width(12.dp))
        BodyText(text, Modifier.weight(1f), Ink.White)
    }
}

@Composable
fun ConfirmButton(label: String, onConfirm: () -> Unit) {
    var armed by remember { mutableStateOf(false) }
    BrutalButton(
        label = if (armed) "TAP AGAIN TO CONFIRM" else label,
        onClick = {
            if (armed) {
                armed = false
                onConfirm()
            } else {
                armed = true
            }
        },
        modifier = Modifier.fillMaxWidth(),
        fill = if (armed) Ink.White else Ink.Charcoal,
        textColor = if (armed) Ink.Black else Ink.White,
        shadow = Ink.Slate,
        fontSize = 15.sp
    )
}
