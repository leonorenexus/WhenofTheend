package com.`when`.oftheend

import android.app.Application
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val store = CheckInStore(app)

    var screen by mutableStateOf(Screen.HOME)

    var snapshot by mutableStateOf(store.snapshot())
        private set

    var history by mutableStateOf(store.history())
        private set

    var contacts by mutableStateOf(store.contacts())
        private set

    var settings by mutableStateOf(store.settings())
        private set

    var recovered by mutableStateOf(store.recovered)
        private set

    fun refresh() {
        snapshot = store.snapshot()
    }

    fun checkIn() {
        store.checkIn()
        history = store.history()
        refresh()
    }

    fun addContact(name: String, phone: String, pref: String, consent: Boolean) {
        if (!consent || contacts.size >= 5) return
        val c = Contact(System.currentTimeMillis(), name.take(40), phone.take(24), pref, true)
        store.saveContacts(contacts + c)
        contacts = store.contacts()
    }

    fun removeContact(id: Long) {
        store.saveContacts(contacts.filter { it.id != id })
        contacts = store.contacts()
    }

    fun setInterval(hours: Int) {
        store.setInterval(hours)
        settings = store.settings()
        refresh()
    }

    fun setHaptics(on: Boolean) {
        store.setHaptics(on)
        settings = store.settings()
    }

    fun setReduceMotion(on: Boolean) {
        store.setReduceMotion(on)
        settings = store.settings()
    }

    fun clearHistory() {
        store.clearHistory()
        history = store.history()
    }

    fun eraseAll() {
        store.eraseAll()
        history = store.history()
        contacts = store.contacts()
        settings = store.settings()
        recovered = false
        refresh()
    }

    fun dismissRecovered() {
        recovered = false
    }

    /** True if the user asked for reduced motion OR Android's animation scale is 0. */
    fun motionReduced(): Boolean {
        if (settings.reduceMotion) return true
        return try {
            Settings.Global.getFloat(
                getApplication<Application>().contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }
}
