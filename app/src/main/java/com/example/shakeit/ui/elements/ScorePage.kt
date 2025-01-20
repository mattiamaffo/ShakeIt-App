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
    val avatars = remember { mutableStateMapOf<String, Int>() }

    // Load leaderboard data
    LaunchedEffect(Unit) {
        authRepository.getLeaderboardData(
            onSuccess = { leaderboard ->
                miniGameData.value = leaderboard
                isLoading.value = false

                // Preload avatars
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
                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = "SCORE",
                    style = Typography.titleLarge,
                    fontSize = 25.sp,
                    color = Color(0xFFF9A825),
                    modifier = Modifier.offset(y = 10.dp)
                )

                Spacer(modifier = Modifier.height(25.dp))

                // Top 3 players based on current page
                val currentGameData = miniGameData.value.getOrNull(pagerState.currentPage)
                val topPlayers = currentGameData?.scores?.take(3) ?: emptyList()

                // Order top players by score
                val topPlayersReordered = listOfNotNull(
                    topPlayers.getOrNull(1),
                    topPlayers.getOrNull(0),
                    topPlayers.getOrNull(2)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    topPlayersReordered.forEach { (username, score) ->
                        // Compute original index
                        val originalIndex = topPlayers.indexOfFirst { it.first == username }

                        val isFirst = originalIndex == 0

                        TopPlayerCard(
                            position = originalIndex + 1,
                            avatarRes = avatars[username] ?: R.drawable.avatar,
                            playerName = username,
                            score = score,
                            isFirst = isFirst
                        )
                    }
                }


                // Divider
                Divider(
                    color = Color.LightGray,
                    thickness = 3.dp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Pager
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
                        Spacer(modifier = Modifier.height(25.dp))

                        Text(
                            text = currentGameData.name,
                            style = MyTypography.montserratSB,
                            fontSize = 20.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        ScoreList(miniGameScores = currentGameData.scores)
                    }
                }

                // Indicator for the pager
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = (-110).dp),
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
                println("Logout clicked")
                navController.navigate("home")
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(45.dp)
                .offset(y = (-74).dp)
        )
    }
}

@Composable
fun ScoreList(miniGameScores: List<Pair<String, Int>>) {
    val remainingScores = miniGameScores.drop(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        remainingScores.forEachIndexed { index, (playerName, score) ->
            val isTop = index == 0
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
                    .background(Color(0xFF4A148C))
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = 16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF9A825)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 4}",
                        style = MyTypography.montserratSB,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                // Player name
                Text(
                    text = playerName,
                    style = MyTypography.montserratSB,
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f).offset(x = 70.dp),
                    textAlign = TextAlign.Start
                )

                // Score
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
            .width(if (isFirst) 120.dp else 120.dp)
            .padding(vertical = 8.dp)
    ) {
        // Avatar
        Box(contentAlignment = Alignment.TopEnd) {
            Avatar(cSize = if (isFirst) 80 else 60, aSize = if (isFirst) 70 else 50, avatarRes = avatarRes)

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF9A825))
                    .align(Alignment.TopEnd)
                    .padding(1.dp),
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

        Spacer(modifier = Modifier.height(12.dp))


        Text(
            text = playerName,
            style = MyTypography.montserratSB,
            fontSize = 15.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$score",
            style = MyTypography.montserratSB,
            fontSize = 15.sp,
            color = Color.White, // Viola chiaro
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4A148C)) // Viola scuro
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
