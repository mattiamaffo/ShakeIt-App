package com.example.shakeit.ui.navigation

import QrScannerScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.elements.FriendsList
import com.example.shakeit.ui.elements.Games
import com.example.shakeit.ui.elements.HomePage
import com.example.shakeit.ui.elements.Login
import com.example.shakeit.ui.elements.MazeEscapeScreen
import com.example.shakeit.ui.elements.QrGenerationPage
import com.example.shakeit.ui.elements.ReactionDuelScreen
import com.example.shakeit.ui.elements.RegisterPage
import com.example.shakeit.ui.elements.ScorePage
import com.example.shakeit.ui.elements.SettingsPage
import com.example.shakeit.ui.elements.ShakeTheBombScreen
import com.example.shakeit.util.SoundManager

@Composable
fun AppNavigation(navController: NavHostController) {

    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.route) {
                "login", "home", "register", "settings", "games", "qr_generation", "leaderboard", "friends_list" -> {
                    // Reproduce the main theme
                    if (!soundManager.isPlaying(R.raw.menu_sound)) {
                        soundManager.playSound(R.raw.menu_sound)
                    }
                }
                "reactionduel" -> {
                    // Reproduce reaction duel theme
                    if (!soundManager.isPlaying(R.raw.reaction_sound)) {
                        soundManager.playSound(R.raw.reaction_sound)
                    }
                }
                "maze_escape" -> {
                    // Reproduce maze escape theme
                    if (!soundManager.isPlaying(R.raw.maze_escape_sound)) {
                        soundManager.playSound(R.raw.maze_escape_sound)
                    }
                }
                "shakebomb" -> {
                    // Reproduce shake bomb theme
                    val startMs = 0
                    val endMs = 3 * 60 * 1000 + 20 * 1000
                    if (!soundManager.isPlaying(R.raw.shake_sound)) {
                        soundManager.playSound(R.raw.shake_sound, startMs = startMs, endMs = endMs)
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.stopSound()
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(navController = navController)
        }
        composable("home") {
            HomePage(navController = navController, authRepository = AuthRepository())
        }
        composable("register") {
            RegisterPage(navController = navController)
        }
        composable("settings") {
            SettingsPage(navController = navController, authRepository = AuthRepository())
        }

        composable("qr_generation") {
            QrGenerationPage(navController = navController)
        }

        composable("games") {
            Games(navController = navController)
        }

        composable("leaderboard") {
            ScorePage(navController = navController, authRepository = AuthRepository())
        }
        composable("friends_list") {
            FriendsList(navController = navController, authRepository = AuthRepository())
        }
        composable("qr_scanner") {
            QrScannerScreen(navController = navController)
        }
        composable("reactionduel") {
            ReactionDuelScreen(navController = navController)
        }
        composable("shakebomb") {
            ShakeTheBombScreen(navController = navController)
        }
        composable(
            "maze_escape?seed={seed}",
            arguments = listOf(navArgument("seed") { type = NavType.LongType })
        ) { backStackEntry ->
            val seed = backStackEntry.arguments?.getLong("seed") ?: System.currentTimeMillis()
            MazeEscapeScreen(navController = navController, seed = seed)
        }
    }
}
