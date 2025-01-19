package com.example.shakeit.ui.elements

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.ui.theme.LightBlue1
import com.example.shakeit.ui.theme.Pontiac
import com.example.shakeit.ui.theme.Purple1
import com.example.shakeit.ui.theme.Yellow1
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.core.graphics.alpha
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.LightBlue3
import com.example.shakeit.ui.theme.MyTypography
import com.example.shakeit.ui.theme.Purple3
import com.example.shakeit.ui.theme.Yellow2


@Composable
fun HomePage(navController: NavController, authRepository: AuthRepository) {
    val currentScreen = "home"
    val showLogoutDialog = remember { mutableStateOf(false) }
    val isAvatarDialogOpen = remember { mutableStateOf(false) }
    val selectedAvatar = remember { mutableStateOf<Int?>(null) } // Avatar selezionato
    val isLoading = remember { mutableStateOf(true) }

    var selectedGame by remember { mutableStateOf<String?>(null) }
    var isPopupOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authRepository.getCurrentUser { user ->
            if (user != null) {

                authRepository.getUserData(user.uid) { userData ->
                    val avatarName = userData?.get("avatar") as? String
                    println("Avatar recuperato dal database: $avatarName")

                    selectedAvatar.value = when (avatarName) {
                        "avatar" -> R.drawable.avatar
                        "avatar2" -> R.drawable.avatar2
                        "avatar3" -> R.drawable.avatar3
                        else -> null
                    }

                    if (selectedAvatar.value == null) {
                        isAvatarDialogOpen.value = true
                    }
                    isLoading.value = false
                }
            } else {
                navController.navigate("login")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Background()

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = 20.dp)
                    .padding(horizontal = 50.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        modifier = Modifier
                            .offset(x = -(30).dp)
                            .size(70.dp),
                        aSize = 100,
                        avatarRes = selectedAvatar.value ?: R.drawable.avatar
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .offset(x = (30).dp)
                            .size(30.dp)
                            .clickable {
                                navController.navigate("settings")
                            }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Title
                Text(
                    text = selectedGame ?: "Choose a Game",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .offset(y = (-20).dp)
                        .padding(horizontal = 8.dp)
                )

                // Main Blocks
                YellowBlock(
                    title = "Beat your friends in the minigames",
                    subBlockTitle = "Minigames",
                    initialColor = Yellow1,
                    targetColor = Yellow2,
                    iconRes = R.drawable.shake,
                    iconSize = 90,
                    iconBottomOffset = 10,
                    onSubBlockClick = { navController.navigate("games") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp)
                )

                // Row Blocks
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Block(
                            title = "Singleplayer",
                            initialColor = Purple1,
                            targetColor = Purple3,
                            iconRes = R.drawable.icon_robot,
                            extraTopOffset = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 135.dp)
                                .clickable {
                                    when (selectedGame) {
                                        "Reaction Duel" -> navController.navigate("reactionduel?training=false")
                                        "Shake the Bomb" -> navController.navigate("shakebomb?training=false")
                                        "Tilt Maze Escape" -> navController.navigate("maze_escape?seed=${System.currentTimeMillis()}&training=false")
                                        else -> println("Game not implemented yet")
                                    }
                                }
                        )
                        Block(
                            title = "Multiplayer",
                            initialColor = Color.Gray.copy(alpha = 0.5f),
                            targetColor = Color.Gray.copy(alpha = 0.5f),
                            iconRes = R.drawable.icon_friends,
                            extraTopOffset = 6,
                            lockIconRes = R.drawable.ic_lock2,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 135.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Right Column
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ScoreBlock(
                            title = "Score",
                            initialColor = Yellow1,
                            targetColor = Yellow2,
                            onBlockClick = { navController.navigate("leaderboard") },
                            iconRes = R.drawable.icon_score,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(69.dp)
                        )
                        Block(
                            title = "Training",
                            initialColor = LightBlue1,
                            targetColor = LightBlue3,
                            iconRes = R.drawable.icon_practice,
                            extraTopOffset = 6,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).clickable {
                                when (selectedGame) {
                                    "Reaction Duel" -> navController.navigate("reactionduel?training=true")
                                    "Shake the Bomb" -> navController.navigate("shakebomb?training=true")
                                    "Tilt Maze Escape" -> navController.navigate("maze_escape?seed=${System.currentTimeMillis()}&training=true")
                                    else -> println("Game not implemented yet")
                                }
                            }
                        )
                        PlayBlock(
                            title = "PLAY",
                            initialColor = Purple1,
                            targetColor = Purple3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(69.dp)
                                .clickable { isPopupOpen = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(0.dp))

                // Navigation Bar
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
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
                            showLogoutDialog.value = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }

        // Logout Dialog
        if (showLogoutDialog.value) {
            LogoutDialog(
                onConfirm = {
                    authRepository.logoutUser {
                        navController.navigate("login")
                    }
                    showLogoutDialog.value = false
                },
                onDismiss = {
                    showLogoutDialog.value = false
                }
            )
        }

        // Game Selection Popup
        if (isPopupOpen) {
            GameSelectionPopup(
                onGameSelected = { game ->
                    selectedGame = game
                    isPopupOpen = false
                },
                onDismiss = { isPopupOpen = false }
            )
        }

        // Avatar Dialog
        if (isAvatarDialogOpen.value) {
            AvatarSelectionDialog(
                onAvatarSelected = { avatar ->
                    selectedAvatar.value = avatar
                    isAvatarDialogOpen.value = false
                    print("Avatar selezionato: $avatar")
                    authRepository.updateUserAvatar(avatar) // Save the avatar in the database
                },
                onDismissRequest = {
                    isAvatarDialogOpen.value = false
                }
            )
        }
    }
}


@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier
            .height(200.dp)
            .width(500.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Do you want to logout?", style = MyTypography.montserratSB.copy(fontSize = 25.sp), lineHeight = 30.sp)
        },
        confirmButton = {
            TextButton(onClick = onConfirm, Modifier.offset(y= 40.dp)) {
                Text(text = "Yes", style = MyTypography.montserratSB.copy(fontSize = 30.sp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, Modifier.offset(y= 40.dp)) {
                Text(text = "No", style = MyTypography.montserratSB.copy(fontSize = 30.sp))
            }
        }
    )
}

@Composable
fun GameSelectionPopup(onGameSelected: (String) -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .width(300.dp)
                .height(200.dp)
                .offset(y = (20).dp)
                .padding(20.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val games = listOf("Reaction Duel", "Shake the Bomb", "Tilt Maze Escape")
            val icons = listOf(
                R.drawable.reaction_duel,
                R.drawable.shake_the_bomb,
                R.drawable.maze_escape
            )

            games.forEachIndexed { index, game ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable { onGameSelected(game) }
                ) {
                    Image(
                        painter = painterResource(id = icons[index]),
                        contentDescription = "$game Icon",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = game, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Block(
    title: String,
    initialColor: Color = Color.White,
    targetColor: Color = Color.White,
    iconRes: Int? = null,
    iconSize: Int = 60,
    extraBottomOffset: Int = 0,
    extraTopOffset: Int = 0,
    fontSize: Int = 17,
    modifier: Modifier = Modifier,
    lockIconRes: Int? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = modifier
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = title,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black,
            fontFamily = Pontiac,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = (-extraTopOffset).dp, x = 8.dp)
                .padding(top = 8.dp)
        )

        if (iconRes != null) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$title Icon",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(iconSize.dp)
                    .offset(y = extraBottomOffset.dp)
                    .alpha(if (lockIconRes != null) 0.5f else 1f)
            )
        }

        if (lockIconRes != null) {
            Image(
                painter = painterResource(id = lockIconRes),
                contentDescription = "Lock Icon",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(30.dp)
                    .offset(x = 8.dp, y = (-8).dp)
            )
        }
    }
}


@Composable
fun PlayBlock(
    title: String,
    initialColor: Color = Color.White,
    targetColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        modifier = modifier
            .background(color = animatedColor, shape = RoundedCornerShape(20.dp))
            .padding(13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 23.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontFamily = Pontiac
        )
    }
}

@Composable
fun ScoreBlock(
    title: String,
    initialColor: Color = Color.White,
    targetColor: Color = Color.White,
    onBlockClick: () -> Unit = {},
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        modifier = modifier
            .background(color = animatedColor, shape = RoundedCornerShape(20.dp))
            .padding(14.dp)
            .clickable { onBlockClick() },
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$title Icon",
                modifier = Modifier
                    .size(60.dp)
                    .weight(1f)
            )

            Text(
                text = title,
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontFamily = Pontiac,
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 5.dp)
            )
        }
    }
}

@Composable
fun YellowBlock(
    title: String,
    subBlockTitle: String,
    initialColor: Color = Color.White,
    targetColor: Color = Color.White,
    subBlockColor: Color = Purple1,
    iconRes: Int? = null,
    iconSize: Int = 80,
    iconBottomOffset: Int = 10,
    subBlockOffset: Int = 10,
    onSubBlockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = initialColor,
        targetValue = targetColor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(color = animatedColor, shape = RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = Pontiac,
            maxLines = 3,
            lineHeight = 20.sp,
            softWrap = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(350.dp)
                .padding(start = subBlockOffset.dp, top = 8.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(40.dp)
                .width(100.dp)
                .offset(x = subBlockOffset.dp)
                .background(color = subBlockColor, shape = RoundedCornerShape(15.dp))
                .clickable { onSubBlockClick() }
                .padding(8.dp)
        ) {
            Text(
                text = subBlockTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Pontiac,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        if (iconRes != null) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "Icon",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(iconSize.dp)
                    .offset(y = iconBottomOffset.dp)
            )
        }
    }
}



