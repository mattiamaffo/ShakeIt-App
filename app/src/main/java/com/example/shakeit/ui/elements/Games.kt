package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.ui.theme.MyTypography
import com.example.shakeit.ui.theme.Pontiac

@Composable
fun Games(navController: NavController) {
    // Lista dei giochi
    val games = listOf(
        Triple("Memory Rush", "A memory game where players replicate a sequence of colors or images that gets progressively harder.", R.drawable.memory_rush),
        Triple("Shake the bomb", "A challenge where players must shake the phone in specific ways to defuse a virtual bomb before time runs out.", R.drawable.shake_the_bomb),
        Triple("Maze escape", "A maze navigation game where players tilt the phone to guide a ball through obstacles and reach the exit.", R.drawable.maze_escape),
        Triple("Reaction duel", "A reflex based game where players tap the screen as fast as possible after a random visual or audio signal.", R.drawable.reaction_duel)
    )

    val currentScreen = "games"

    var currentIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GAMES",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                fontFamily = Pontiac
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProgressBar(currentIndex = currentIndex, total = games.size)

            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                // sx
                NavigationArrow(
                    iconRes = R.drawable.arrow_left,
                    onClick = {
                        if (currentIndex > 0) currentIndex--
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 5.dp)
                )

                // Card
                GameCard(
                    title = games[currentIndex].first,
                    description = games[currentIndex].second,
                    imageRes = games[currentIndex].third,
                    modifier = Modifier
                        .width(250.dp)
                        .height(300.dp)
                        .padding(horizontal = 16.dp)
                )

                // dx
                NavigationArrow(
                    iconRes = R.drawable.arrow_right,
                    onClick = {
                        if (currentIndex < games.size - 1) currentIndex++
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 5.dp)
                )
            }
        }
        NavBar(
            icons = listOf(
                Pair(R.drawable.arrow_back, "back"),
                Pair(R.drawable.ic_leaderbord, "leaderboard"),
                Pair(R.drawable.ic_home, "home"),
                Pair(R.drawable.ic_chat, "friends_list")
            ),
            currentScreen = currentScreen,
            onIconClick = { screenName ->
                println("Navigating to $screenName")
                navController.navigate(screenName)
            },
            onLogout = {
                println("Logout clicked") // Debug
                navController.navigate("home")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
        )
    }
}

@Composable
fun ProgressBar(currentIndex: Int, total: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color.Gray, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = (currentIndex + 1) / total.toFloat())
                .height(8.dp)
                .background(Color(0xFF9575CD), shape = CircleShape)
        )
    }
}

@Composable
fun GameCard(
    title: String,
    description: String,
    imageRes: Int? = null, // Risorsa immagine opzionale
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {

            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Game Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            // Nome del gioco
            Text(
                text = title,
                style = MyTypography.montserratSB,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(top = 30.dp)
            )

            // Descrizione del gioco
            Text(
                text = description,
                fontSize = 10.sp,
                fontFamily = Pontiac,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}




@Composable
fun NavigationArrow(iconRes: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = iconRes),
        contentDescription = "Navigation Arrow",
        modifier = modifier
            .size(60.dp)
            .clickable { onClick() }
    )
}


