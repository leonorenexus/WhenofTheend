package com.`when`.oftheend

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun two(n: Long): String = n.toString().padStart(2, '0')

fun fmtDate(ms: Long): String =
    SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault()).format(Date(ms)).uppercase()

fun fmtTime(ms: Long): String =
    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(ms))

fun fmtDateTime(ms: Long): String =
    SimpleDateFormat("EEE dd MMM HH:mm", Locale.getDefault()).format(Date(ms)).uppercase()

fun fmtDuration(ms: Long): String {
    val totalMin = ms / 60_000L
    return "${totalMin / 60}H ${totalMin % 60}M"
}
