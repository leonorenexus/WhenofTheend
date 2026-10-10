package com.`when`.oftheend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistoryScreen(vm: AppViewModel) {
    val items = vm.history.sortedDescending()
    ScreenFrame("CHECK-IN HISTORY", onBack = { vm.screen = Screen.HOME }, scroll = false) {
        if (items.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                BrutalSurface(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "NO CHECK-INS RECORDED YET", color = Ink.White, fontFamily = Ink.Sans,
                            fontWeight = FontWeight.Black, fontSize = 18.sp
                        )
                        Box(Modifier.height(8.dp))
                        BodyText("TAP \"I AM STILL HERE\" ON THE HOME SCREEN. EACH CHECK-IN APPEARS HERE (LAST 200 KEPT).")
                    }
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                itemsIndexed(items) { i, t ->
                    TimelineRow(
                        time = t,
                        previous = items.getOrNull(i + 1),
                        isLast = i == items.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(time: Long, previous: Long?, isLast: Boolean) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(
            Modifier
                .width(28.dp)
                .fillMaxHeight()
                .drawBehind {
                    val lineW = 3.dp.toPx()
                    val nodeS = 16.dp.toPx()
                    val nodeTop = 6.dp.toPx()
                    val lineBottom = if (isLast) nodeTop + nodeS / 2f else size.height
                    drawRect(
                        color = Ink.White,
                        topLeft = Offset(size.width / 2f - lineW / 2f, 0f),
                        size = Size(lineW, lineBottom)
                    )
                    drawRect(
                        color = Ink.White,
                        topLeft = Offset(size.width / 2f - nodeS / 2f, nodeTop),
                        size = Size(nodeS, nodeS)
                    )
                }
        )
        Column(Modifier.weight(1f).padding(start = 8.dp, bottom = 20.dp)) {
            Text(
                fmtDate(time), color = Ink.White, fontFamily = Ink.Sans,
                fontWeight = FontWeight.Black, fontSize = 18.sp
            )
            Text(
                fmtTime(time), color = Ink.White, fontFamily = Ink.Mono,
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
            Label(
                if (previous != null) "+${fmtDuration(time - previous)} AFTER PREVIOUS" else "FIRST RECORDED CHECK-IN"
            )
        }
    }
}
