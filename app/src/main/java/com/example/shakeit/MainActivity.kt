package com.example.shakeit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.shakeit.ui.navigation.AppNavigation
import com.example.shakeit.ui.theme.ShakeItTheme
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "OpenCV initialized successfully", Toast.LENGTH_SHORT).show()
        }
        setContent {
            ShakeItTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
