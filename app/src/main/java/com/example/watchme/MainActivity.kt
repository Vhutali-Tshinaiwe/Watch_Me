package com.example.watchme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.watchme.ui.navigation.WatchMeNavGraph
import com.example.watchme.ui.theme.WatchMeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            WatchMeTheme(darkTheme = darkTheme) {
                WatchMeNavGraph(
                    darkTheme = darkTheme,
                    onToggleDarkTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
