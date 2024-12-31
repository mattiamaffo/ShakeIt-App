package com.example.shakeit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.shakeit.ui.navigation.AppNavigation
import com.example.shakeit.ui.theme.ShakeItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShakeItTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
