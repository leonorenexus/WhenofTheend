package com.`when`.oftheend

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.provider.Settings
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local-only persistence. Each check-in stores THREE anchors:
 *  - wall clock time (shown to the user, fallback after reboot)
 *  - SystemClock.elapsedRealtime() (monotonic, immune to clock changes, resets at reboot)
 *  - the device boot counter (tells us whether the monotonic anchor is still valid)
 */
class CheckInStore(private val ctx: Context) {

    private val p: SharedPreferences = ctx.getSharedPreferences("weoe_state", Context.MODE_PRIVATE)

    var recovered: Boolean = false
        private set

    init {
        try {
            p.getLong(K_WALL, -1L)
            p.getLong(K_ELAPSED, 0L)
            p.getInt(K_BOOT, -2)
            p.getInt(K_INTERVAL, 48)
            p.getString(K_HISTORY, "")
            p.getString(K_CONTACTS, "[]")
            p.getBoolean(K_HAPTICS, true)
            p.getBoolean(K_REDUCE, false)
        } catch (e: Exception) {
            p.edit().clear().commit()
            recovered = true
        }
        if (p.getLong(K_WALL, -1L) <= 0L) {
            if (p.contains(K_WALL)) recovered = true
            checkIn(record = false)
        }
    }

    private fun bootCount(): Int = try {
        Settings.Global.getInt(ctx.contentResolver, Settings.Global.BOOT_COUNT, -1)
    } catch (e: Exception) {
        -1
    }

    fun intervalHours(): Int = try {
        p.getInt(K_INTERVAL, 48).coerceIn(1, 168)
    } catch (e: Exception) {
        48
    }

    private fun elapsedMs(): Long {
        val wall = p.getLong(K_WALL, -1L)
        val boot = p.getInt(K_BOOT, -2)
        if (boot >= 0 && boot == bootCount()) {
            val e = SystemClock.elapsedRealtime() - p.getLong(K_ELAPSED, 0L)
            if (e >= 0L) return e
        }
        // After a reboot (or when no boot counter is available) the wall clock is the only reference.
        return (System.currentTimeMillis() - wall).coerceAtLeast(0L)
    }

    fun snapshot(): Snapshot {
        val interval = intervalHours() * HOUR_MS
        val elapsed = elapsedMs()
        val remaining = interval - elapsed
        val window = minOf(6L * HOUR_MS, interval / 4L)
        val status = when {
            remaining <= 0L -> CheckInStatus.OVERDUE
            remaining <= window -> CheckInStatus.REQUIRED
            else -> CheckInStatus.ACTIVE
        }
        return Snapshot(
            status = status,
            remainingMs = remaining.coerceAtLeast(0L),
            overdueMs = (-remaining).coerceAtLeast(0L),
            lastCheckInWall = p.getLong(K_WALL, 0L),
            deadlineWall = System.currentTimeMillis() + remaining,
            elapsedFraction = (elapsed.toFloat() / interval.toFloat()).coerceIn(0f, 1f)
        )
    }

    fun checkIn(record: Boolean = true) {
        val wall = System.currentTimeMillis()
        val e = p.edit()
            .putLong(K_WALL, wall)
            .putLong(K_ELAPSED, SystemClock.elapsedRealtime())
            .putInt(K_BOOT, bootCount())
        if (record) {
            val list = (history() + wall).takeLast(MAX_HISTORY)
            e.putString(K_HISTORY, list.joinToString(","))
        }
        e.commit()
    }

    fun history(): List<Long> =
        (p.getString(K_HISTORY, "") ?: "")
            .split(",")
            .mapNotNull { it.trim().toLongOrNull() }
            .filter { it > 0L }

    fun clearHistory() {
        p.edit().putString(K_HISTORY, "").apply()
    }

    fun eraseAll() {
        p.edit().clear().commit()
        recovered = false
        checkIn(record = false)
    }

    fun contacts(): List<Contact> = try {
        val arr = JSONArray(p.getString(K_CONTACTS, "[]") ?: "[]")
        (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            Contact(
                id = o.getLong("id"),
                name = o.getString("name"),
                phone = o.getString("phone"),
                pref = o.optString("pref", "CALL"),
                consent = o.optBoolean("consent", false)
            )
        }
    } catch (e: Exception) {
        emptyList()
    }

    fun saveContacts(list: List<Contact>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.name)
                    .put("phone", it.phone)
                    .put("pref", it.pref)
                    .put("consent", it.consent)
            )
        }
        p.edit().putString(K_CONTACTS, arr.toString()).apply()
    }

    fun settings(): AppSettings = AppSettings(
        intervalHours = intervalHours(),
        haptics = p.getBoolean(K_HAPTICS, true),
        reduceMotion = p.getBoolean(K_REDUCE, false)
    )

    fun setInterval(hours: Int) { p.edit().putInt(K_INTERVAL, hours.coerceIn(1, 168)).apply() }
    fun setHaptics(on: Boolean) { p.edit().putBoolean(K_HAPTICS, on).apply() }
    fun setReduceMotion(on: Boolean) { p.edit().putBoolean(K_REDUCE, on).apply() }

    companion object {
        private const val HOUR_MS = 3_600_000L
        private const val MAX_HISTORY = 200
        private const val K_WALL = "last_wall"
        private const val K_ELAPSED = "last_elapsed"
        private const val K_BOOT = "last_boot"
        private const val K_INTERVAL = "interval_hours"
        private const val K_HISTORY = "history"
        private const val K_CONTACTS = "contacts"
        private const val K_HAPTICS = "haptics"
        private const val K_REDUCE = "reduce_motion"
    }
}
