package com.example.shakeit.ui.elements


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.ui.theme.Typography
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

data class MinigameData(
    val name: String,
    val scores: List<Pair<String, Int>>
)


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScorePage(navController: NavController, authRepository: AuthRepository) {
    val currentScreen = "leaderboard"
    val pagerState = rememberPagerState()
    val miniGameData = remember { mutableStateOf<List<MinigameData>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val avatars = remember { mutableStateMapOf<String, Int>() } // Per memorizzare gli avatar recuperati

    // Recupera i dati della classifica
    LaunchedEffect(Unit) {
        authRepository.getLeaderboardData(
            onSuccess = { leaderboard ->
                miniGameData.value = leaderboard
                isLoading.value = false

                // Recupera gli avatar per i top giocatori
                leaderboard.forEach { game ->
                    game.scores.take(3).forEach { (username, _) ->
                        if (!avatars.containsKey(username)) {
                            authRepository.getUserDataByUsername(username) { userData ->
                                val avatarName = userData?.get("avatar") as? String
                                val avatarRes = when (avatarName) {
                                    "avatar" -> R.drawable.avatar
                                    "avatar2" -> R.drawable.avatar2
                                    "avatar3" -> R.drawable.avatar3
                                    else -> R.drawable.avatar // Default avatar
                                }
                                avatars[username] = avatarRes
                            }
                        }
                    }
                }
            },
            onFailure = { error ->
                println("Errore nel recupero della classifica: $error")
                isLoading.value = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background
        Background()

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Titolo
                Text(
                    text = "SCORE",
                    style = Typography.titleLarge,
                    color = Color(0xFFF9A825),
                    modifier = Modifier.offset(y = 10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sezione Top 3
                val topPlayers = miniGameData.value.firstOrNull()?.scores?.take(3) ?: emptyList()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    topPlayers.forEachIndexed { index, (username, score) ->
                        val isFirst = index == 0
                        val avatarRes = avatars[username] ?: R.drawable.avatar

                        TopPlayerCard(
                            position = index + 1,
                            avatarRes = avatarRes,
                            playerName = username,
                            score = score,
                            isFirst = isFirst
                        )
                    }
                }

                // Divisore
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Pager per i minigiochi
                HorizontalPager(
                    count = miniGameData.value.size,
                    modifier = Modifier.weight(1f),
                    state = pagerState
                ) { page ->
                    val currentGameData = miniGameData.value[page]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentGameData.name,
                            style = MyTypography.montserratSB,
                            fontSize = 20.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ScoreList(miniGameScores = currentGameData.scores)
                    }
                }

                // Indicatore del Pager
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = (-55).dp),
                    activeColor = Color(0xFFF9A825),
                    inactiveColor = Color.Gray,
                    indicatorWidth = 5.dp,
                    indicatorHeight = 5.dp,
                    spacing = 8.dp
                )
            }
        }

        NavBar(
            icons = listOf(
                Pair(R.drawable.back_vector, "back"),
                Pair(R.drawable.chart_icon, "leaderboard"),
                Pair(R.drawable.home_icon, "home"),
                Pair(R.drawable.chat_icon, "friends_list")
            ),
            currentScreen = currentScreen,
            onIconClick = { screenName ->
                println("Navigating to $screenName")
                navController.navigate(screenName)
            },
            onLogout = {
                println("Logout clicked")
                navController.navigate("home")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp)
        )
    }
}

@Composable
fun ScoreList(miniGameScores: List<Pair<String, Int>>) {
    val remainingScores = miniGameScores.drop(3) // Rimuovi i primi 3 giocatori dalla lista

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        remainingScores.forEachIndexed { index, (playerName, score) ->
            val isTop = index == 0 // Primo elemento della lista restante
            val isBottom = index == remainingScores.size - 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isTop) 8.dp else 0.dp,
                            topEnd = if (isTop) 8.dp else 0.dp,
                            bottomStart = if (isBottom) 8.dp else 0.dp,
                            bottomEnd = if (isBottom) 8.dp else 0.dp
                        )
                    )
                    .background(Color(0xFF4A148C)) // Viola scuro
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Posizione
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = 16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9A825)), // Giallo
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 4}", // Posizione (inizia dal quarto posto)
                        style = MyTypography.montserratSB,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                // Nome giocatore
                Text(
                    text = playerName,
                    style = MyTypography.montserratSB,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f).offset(x = 70.dp),
                    textAlign = TextAlign.Start
                )

                // Punteggio
                Text(
                    text = "$score",
                    style = MyTypography.montserratSB,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.offset(x = (-16).dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
@Composable
fun TopPlayerCard(
    position: Int,
    avatarRes: Int,
    playerName: String,
    score: Int,
    isFirst: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(if (isFirst) 100.dp else 80.dp)
            .padding(vertical = 8.dp)
    ) {
        // Avatar
        Box(contentAlignment = Alignment.TopEnd) {
            Avatar(cSize = if (isFirst) 90 else 70, aSize = if (isFirst) 80 else 60, avatarRes = avatarRes)
            // Posizione
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9A825)) // Giallo
                    .align(Alignment.TopEnd)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$position",
                    fontSize = 15.sp,
                    style = Typography.titleLarge,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nome giocatore
        Text(
            text = playerName,
            style = MyTypography.montserratSB,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Punteggio
        Text(
            text = "$score",
            style = MyTypography.montserratSB,
            fontSize = 17.sp,
            color = Color.White, // Viola chiaro
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4A148C)) // Viola scuro
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
