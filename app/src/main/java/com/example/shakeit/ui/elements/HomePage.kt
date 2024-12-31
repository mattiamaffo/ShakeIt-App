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
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            // Contenuto principale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    modifier = Modifier.size(48.dp),
                    avatarRes = selectedAvatar.value ?: R.drawable.avatar
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = "Settings",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            navController.navigate("settings")
                        }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                YellowBlock(
                    title = "Beat your friends in the minigames",
                    subBlockTitle = "MiniGames",
                    initialColor = Yellow1,
                    targetColor = Yellow2,
                    iconRes = R.drawable.shake,
                    iconSize = 80,
                    iconBottomOffset = 10,
                    onSubBlockClick = { navController.navigate("games") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seconda sezione (due colonne)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Colonna sinistra
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Block(
                            title = "Play Vs Robot",
                            initialColor = Purple1,
                            targetColor = Purple3,
                            iconRes = R.drawable.icon_robot,
                            extraTopOffset = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )
                        Block(
                            title = "Play Vs Friend",
                            initialColor = LightBlue1,
                            targetColor = LightBlue3,
                            iconRes = R.drawable.icon_friends,
                            extraTopOffset = 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ScoreBlock(
                            title = "Score",
                            initialColor = Yellow1,
                            targetColor = Yellow2,
                            onBlockClick = { navController.navigate("leaderboard") },
                            iconRes = R.drawable.icon_score,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        )
                        Block(
                            title = "Practice",
                            initialColor = LightBlue1,
                            targetColor = LightBlue3,
                            iconRes = R.drawable.icon_practice,
                            fontSize = 18,
                            extraTopOffset = 10,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                        PlayBlock(
                            title = "PLAY",
                            initialColor = Purple1,
                            targetColor = Purple3,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        )
                    }
                }
            }
            NavBar(
                icons = listOf(
                    Pair(R.drawable.back_vector, "back"),
                    Pair(R.drawable.chart_icon, "leaderboard"),
                    Pair(R.drawable.home_icon, "home"),
                    Pair(R.drawable.chat_icon, "chat")
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
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp)
            )

            if (showLogoutDialog.value) {
                LogoutDialog(
                    onConfirm = {
                        authRepository.logoutUser {
                            navController.navigate("login") // Logout e reindirizzamento
                        }
                        showLogoutDialog.value = false
                    },
                    onDismiss = {
                        showLogoutDialog.value = false // Chiudi il popup
                    }
                )
            }
        }
        if (isAvatarDialogOpen.value) {
            AvatarSelectionDialog(
                onAvatarSelected = { avatar ->
                    selectedAvatar.value = avatar
                    isAvatarDialogOpen.value = false
                    print("Avatar selezionato: $avatar")
                    authRepository.updateUserAvatar(avatar) // Salva l'avatar nel database
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
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Do you want to logout?", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes", style = MyTypography.montserratSB.copy(fontSize = 18.sp))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "No", style = MyTypography.montserratSB.copy(fontSize = 18.sp))
            }
        }
    )
}


@Composable
fun Block(
    title: String,
    initialColor: Color = Color.White,
    targetColor: Color = Color.White,
    iconRes: Int? = null,
    iconSize: Int = 80,
    extraBottomOffset: Int = 0,
    extraTopOffset: Int = 0,
    fontSize: Int = 14,
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
            .background(color = animatedColor, shape = RoundedCornerShape(20.dp)) // Sfondo e bordi arrotondati
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
                .align(Alignment.TopCenter)
                .offset(y = (-extraTopOffset).dp)
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
            .padding(16.dp),
        contentAlignment = Alignment.Center // Centra il testo
    ) {
        Text(
            text = title,
            fontSize = 25.sp,
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
            .padding(16.dp)
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
                    .size(40.dp)
                    .weight(1f)
            )

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black,
                fontFamily = Pontiac,
                modifier = Modifier
                    .weight(3f)
                    .padding(start = 10.dp)
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
    subBlockOffset: Int = 8,
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
            .height(120.dp)
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
            softWrap = true,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(300.dp)
                .padding(start = subBlockOffset.dp, top = 8.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .height(40.dp)
                .width(80.dp)
                .offset(x = subBlockOffset.dp)
                .background(color = subBlockColor, shape = RoundedCornerShape(12.dp))
                .clickable { onSubBlockClick() }
                .padding(8.dp)
        ) {
            Text(
                text = subBlockTitle,
                fontSize = 10.sp,
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



