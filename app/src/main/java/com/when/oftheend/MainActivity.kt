package com.`when`.oftheend

import android.graphics.Color as AColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AColor.TRANSPARENT)
        )

        // One tick per second, only while the screen is visible (STARTED). No background work.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    vm.refresh()
                    delay(1000L - (System.currentTimeMillis() % 1000L))
                }
            }
        }

        setContent {
            WhenTheme { App(vm) }
        }
    }
}

@Composable
fun App(vm: AppViewModel) {
    BackHandler(enabled = vm.screen != Screen.HOME) { vm.screen = Screen.HOME }
    val fade = if (vm.motionReduced()) 0 else 160
    Crossfade(targetState = vm.screen, animationSpec = tween(fade), label = "nav") { screen ->
        when (screen) {
            Screen.HOME ->
                if (vm.snapshot.status == CheckInStatus.OVERDUE) OverdueScreen(vm) else HomeScreen(vm)
            Screen.HISTORY -> HistoryScreen(vm)
            Screen.CONTACTS -> ContactsScreen(vm)
            Screen.SETTINGS -> SettingsScreen(vm)
        }
    }
}
