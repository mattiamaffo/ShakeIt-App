package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography

data class Friend(val name: String, val avatarRes: Int, val section: String = "")

private fun loadCurrentUserAvatar(
    authRepository: AuthRepository,
    userId: String,
    selectedAvatar: MutableState<Int?>,
    onComplete: () -> Unit
) {
    authRepository.getUserData(userId) { userData ->
        val avatarName = userData?.get("avatar") as? String
        println("Avatar corrente recuperato dal database: $avatarName")

        val currentAvatarRes = when (avatarName) {
            "avatar" -> R.drawable.avatar
            "avatar2" -> R.drawable.avatar2
            "avatar3" -> R.drawable.avatar3
            else -> R.drawable.avatar
        }
        selectedAvatar.value = currentAvatarRes
        onComplete()
    }
}

private fun loadFriendsList(
    authRepository: AuthRepository,
    userId: String,
    friends: MutableState<List<Friend>>,
    isLoading: MutableState<Boolean>
) {
    authRepository.getFriends(
        userId = userId,
        onSuccess = { friendsList ->
            val tempFriends = mutableListOf<Friend>()

            friendsList.forEach { friendId ->
                authRepository.getUserData(friendId) { friendData ->
                    val username = friendData?.get("username") as? String ?: "Unknown"
                    val friendAvatarName = friendData?.get("avatar") as? String
                    val friendAvatarRes = when (friendAvatarName) {
                        "avatar" -> R.drawable.avatar
                        "avatar2" -> R.drawable.avatar2
                        "avatar3" -> R.drawable.avatar3
                        else -> R.drawable.avatar
                    }

                    // Compute the right section
                    val section = username.firstOrNull()?.uppercase() ?: ""

                    // Debug
                    println("Loaded friend: username=$username, section=$section")

                    tempFriends.add(
                        Friend(
                            name = username,
                            avatarRes = friendAvatarRes,
                            section = section
                        )
                    )

                    if (tempFriends.size == friendsList.size) {
                        val sortedFriends = tempFriends.sortedBy { it.name }
                        friends.value = sortedFriends
                        isLoading.value = false
                    }
                }
            }

            if (friendsList.isEmpty()) {
                friends.value = emptyList()
                isLoading.value = false
            }
        },
        onFailure = { error ->
            println("Error fetching friends: $error")
            isLoading.value = false
        }
    )
}

private fun addNewFriend(
    authRepository: AuthRepository,
    userId: String,
    friendUsername: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    authRepository.getUserDataByUsername(
        username = friendUsername,
        onResult = { userData ->
            if (userData != null) {
                val friendId = userData["documentId"] as? String ?: ""
                if (friendId.isNotEmpty()) {
                    authRepository.addMutualFriend(
                        userId = userId,
                        friendId = friendId,
                        onSuccess = onSuccess,
                        onFailure = onFailure
                    )
                } else {
                    onFailure("Friend document ID not found")
                }
            } else {
                onFailure("Username not found")
            }
        }
    )
}



@Composable
fun FriendsList(navController: NavController, authRepository: AuthRepository) {
    val currentScreen = "friends_list"
    val friends = remember { mutableStateOf<List<Friend>>(emptyList()) }
    val selectedAvatar = remember { mutableStateOf<Int?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val userId = authRepository.auth.currentUser?.uid

    val showAddFriendDialog = remember { mutableStateOf(false) }
    val newFriendUsername = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (userId != null) {
            loadCurrentUserAvatar(authRepository, userId, selectedAvatar) {
                loadFriendsList(authRepository, userId, friends, isLoading)
            }
        } else {
            navController.navigate("login")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Background()

        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        modifier = Modifier.size(60.dp),
                        aSize = 50,
                        avatarRes = selectedAvatar.value ?: R.drawable.avatar
                    )

                    Text(
                        text = "FRIENDS",
                        style = MyTypography.montserratSB,
                        fontSize = 26.sp,
                        color = Color(0xFFF9A825),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(x = (-5).dp)
                    )

                    Text(
                        text = "+",
                        style = MyTypography.montserratSB,
                        fontSize = 40.sp,
                        color = Color(0xFFF9A825),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                showAddFriendDialog.value = true
                            }
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    var currentSection = ""

                    friends.value.forEach { friend ->
                        val friendSection = friend.section // Use the correct section
                        if (friendSection != currentSection) {
                            currentSection = friendSection
                            item {
                                Text(
                                    text = friendSection,
                                    style = MyTypography.montserratSB,
                                    fontSize = 18.sp,
                                    color = Color(0xFFF9A825),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                        item {
                            FriendItem(friend = friend)
                        }
                    }
                }
            }
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
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(47.dp)
                .offset(y = (-70).dp)
        )
    }

    if (showAddFriendDialog.value) {
        AlertDialog(
            onDismissRequest = { showAddFriendDialog.value = false },
            title = {
                Text(text = "Add Friend", style = MyTypography.montserratSB)
            },
            text = {
                Column {
                    TextField(
                        value = newFriendUsername.value,
                        onValueChange = { newFriendUsername.value = it },
                        label = { Text("Enter username") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (userId != null) {
                            addNewFriend(
                                authRepository = authRepository,
                                userId = userId,
                                friendUsername = newFriendUsername.value,
                                onSuccess = {
                                    println("Friend added successfully!")
                                    showAddFriendDialog.value = false
                                    loadFriendsList(authRepository, userId, friends, isLoading)
                                },
                                onFailure = { error ->
                                    println("Failed to add friend: $error")
                                    showAddFriendDialog.value = false
                                }
                            )
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FriendItem(friend: Friend) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Avatar(
            modifier = Modifier.size(30.dp),
            avatarRes = friend.avatarRes)

        Spacer(modifier = Modifier.width(16.dp))

        // Name
        Text(
            text = friend.name,
            style = MyTypography.montserratSB,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

