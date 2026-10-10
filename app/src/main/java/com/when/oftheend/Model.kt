package com.`when`.oftheend

enum class Screen { HOME, HISTORY, CONTACTS, SETTINGS }

enum class CheckInStatus(val label: String, val short: String) {
    ACTIVE("ACTIVE", "ACTIVE"),
    REQUIRED("CHECK-IN REQUIRED", "DUE SOON"),
    OVERDUE("OVERDUE", "OVERDUE")
}

data class Snapshot(
    val status: CheckInStatus,
    val remainingMs: Long,
    val overdueMs: Long,
    val lastCheckInWall: Long,
    val deadlineWall: Long,
    val elapsedFraction: Float
)

data class Contact(
    val id: Long,
    val name: String,
    val phone: String,
    val pref: String,
    val consent: Boolean
)

data class AppSettings(
    val intervalHours: Int,
    val haptics: Boolean,
    val reduceMotion: Boolean
)
