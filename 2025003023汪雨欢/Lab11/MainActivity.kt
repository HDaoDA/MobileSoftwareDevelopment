package com.example.sports

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.example.sports.ui.SportsApp
import com.example.sports.ui.SportsTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportsTheme {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                val widthSizeClass = windowSizeClass.widthSizeClass
                SportsApp(windowWidthSizeClass = widthSizeClass)
            }
        }
    }
}