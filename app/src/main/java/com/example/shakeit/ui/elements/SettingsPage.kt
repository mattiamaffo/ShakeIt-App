package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography
import com.example.shakeit.ui.theme.Pontiac
import com.example.shakeit.util.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(navController: NavController, authRepository: AuthRepository) {
    val currentScreen = "settings"
    var username by remember { mutableStateOf("@username") }
    var isEditing by remember { mutableStateOf(false) }
    val isAvatarDialogOpen = remember { mutableStateOf(false) }
    val selectedAvatar = remember { mutableStateOf(R.drawable.avatar) }
    var isSavingName by remember { mutableStateOf(false) }
    val isLogoutDialogOpen = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        authRepository.getCurrentUser { user ->
            if (user != null) {
                authRepository.getUserData(user.uid) { userData ->
                    val avatarName = userData?.get("avatar") as? String
                    val retrievedUsername = userData?.get("username") as? String

                    selectedAvatar.value = when (avatarName) {
                        "avatar" -> R.drawable.avatar
                        "avatar2" -> R.drawable.avatar2
                        "avatar3" -> R.drawable.avatar3
                        else -> R.drawable.avatar
                    }

                    if (!retrievedUsername.isNullOrEmpty()) {
                        username = retrievedUsername
                    }

                    isLoading.value = false
                }
            } else {
                navController.navigate("login")
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Background()

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Avatar
                Avatar(
                    cSize = 100,
                    aSize = 90,
                    avatarRes = selectedAvatar.value ?: R.drawable.avatar
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Username
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            textStyle = MyTypography.montserratSB.copy(
                                fontSize = 25.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    isEditing = false
                                    isSavingName = true
                                    authRepository.updateUsername(
                                        newUsername = username,
                                        onSuccess = {
                                            isSavingName = false
                                            println("Username updated successfully!")
                                        },
                                        onFailure = { error ->
                                            isSavingName = false
                                            println("Error updating username: $error")
                                        }
                                    )
                                }
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                containerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.Center)
                        )
                    } else {
                        Text(
                            text = username,
                            style = MyTypography.montserratSB,
                            fontSize = 25.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))

                // CustomButton
                CustomButton(
                    text = "Change avatar",
                    width = 300,
                    height = 60,
                    fontSize = 24,
                    onClick = {
                        if (!isSavingName) {
                            isAvatarDialogOpen.value = true
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(
                    text = "Change name",
                    width = 300,
                    height = 60,
                    fontSize = 24,
                    onClick = {
                        isEditing = true
                        username = ""
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomButton(
                        text = "Generate QR",
                        width = 220,
                        height = 60,
                        fontSize = 24,
                        onClick = { navController.navigate("qr_generation") }
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFF6200EA), shape = RoundedCornerShape(16.dp))
                            .clickable { navController.navigate("qr_scanner") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            style = MyTypography.montserratSB.copy(fontSize = 30.sp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                CustomButton(
                    text = "Logout",
                    width = 300,
                    height = 60,
                    fontSize = 24,
                    backgroundColor = Color(0xFFE74C3C),
                    textColor = Color.White,
                    onClick = {
                        isLogoutDialogOpen.value = true
                    }
                )
            }
            NavBar(
                icons = listOf(
                    Pair(R.drawable.arrow_back, "home"),
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
                    .offset(y = (-70).dp)
            )
        }

        if (isAvatarDialogOpen.value) {
            AvatarSelectionDialog(
                onAvatarSelected = { avatar ->
                    selectedAvatar.value = avatar
                    isAvatarDialogOpen.value = false
                    authRepository.updateUserAvatar(avatar)
                },
                onDismissRequest = { isAvatarDialogOpen.value = false }
            )
        }

        if (isLogoutDialogOpen.value) {
            AlertDialog(
                onDismissRequest = { isLogoutDialogOpen.value = false },
                title = {
                    Text(text = "Do you want to logout?", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
                },
                confirmButton = {
                    TextButton(onClick = {
                        authRepository.logoutUser {
                            isLogoutDialogOpen.value = false
                            navController.navigate("login")
                        }
                    }) {
                        Text("Yes", style = MyTypography.montserratSB.copy(fontSize = 18.sp))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isLogoutDialogOpen.value = false }) {
                        Text("No", style = MyTypography.montserratSB.copy(fontSize = 18.sp))
                    }
                }
            )
        }

        if (isSavingName) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(300)
                isSavingName = false
            }
        }
    }
}


@Composable
fun AvatarSelectionDialog(
    onAvatarSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismissRequest)
    ) {
        Box(
            modifier = Modifier
                .width(550.dp)
                .height(350.dp)
                .align(Alignment.Center)
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Avatar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(45.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(22.dp),
                    modifier = Modifier.padding(3.dp)
                ) {
                    AvatarOption(R.drawable.avatar, onAvatarSelected)
                    AvatarOption(R.drawable.avatar2, onAvatarSelected)
                    AvatarOption(R.drawable.avatar3, onAvatarSelected)
                }

                Spacer(modifier = Modifier.height(60.dp))
                CustomButton(
                    text = "Cancel",
                    width = 200,
                    height = 50,
                    fontSize = 24,
                    backgroundColor = Color.Gray,
                    textColor = Color.White,
                    onClick = onDismissRequest
                )
            }
        }
    }
}

@Composable
fun AvatarOption(avatarRes: Int, onAvatarSelected: (Int) -> Unit) {
    Image(
        painter = painterResource(id = avatarRes),
        contentDescription = "Avatar Option",
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .clickable { onAvatarSelected(avatarRes) }
    )
}
