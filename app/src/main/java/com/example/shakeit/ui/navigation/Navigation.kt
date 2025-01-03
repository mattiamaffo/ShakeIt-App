package com.example.shakeit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.elements.FriendsList
import com.example.shakeit.ui.elements.Games
import com.example.shakeit.ui.elements.HomePage
import com.example.shakeit.ui.elements.Login
import com.example.shakeit.ui.elements.QrGenerationPage
import com.example.shakeit.ui.elements.RegisterPage
import com.example.shakeit.ui.elements.ScorePage
import com.example.shakeit.ui.elements.SettingsPage

@Composable
fun AppNavigation(navController: NavHostController) {
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
        composable("friends_list") { // Aggiunta della schermata Friends List
            FriendsList(navController = navController, authRepository = AuthRepository())
        }
    }
}
