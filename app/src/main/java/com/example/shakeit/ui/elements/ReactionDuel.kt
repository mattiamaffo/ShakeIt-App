package com.example.shakeit.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography


@Composable
fun ReactionDuelScreen(navController: NavController, isTraining: Boolean) {
    val backgroundColor = remember { mutableStateOf(Color.Gray) }
    val isWaitingForSignal = remember { mutableStateOf(true) }
    val startTime = remember { mutableStateOf(0L) }
    val lives = remember { mutableStateOf(3) }
    val currentRound = remember { mutableStateOf(1) }
    val score = remember { mutableStateOf(0) }
    val feedbackText = remember { mutableStateOf<String?>(null) }
    val isRedIconActive = remember { mutableStateOf(false) }
    val isGreenIconActive = remember { mutableStateOf(false) }
    val showGameOverDialog = remember { mutableStateOf(false) }
    val showExitDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isGamePaused = remember { mutableStateOf(false) }
    val authRepository = remember { AuthRepository() }

    fun resetIconsAndRestartRound() {
        coroutineScope.launch {
            delay(1000L)
            isRedIconActive.value = false
            feedbackText.value = null
            backgroundColor.value = Color.Gray
            startTime.value = 0L
            isWaitingForSignal.value = true
        }
    }

    fun handleGameOver() {
        if (!isTraining) {
            authRepository.updateMinigameScore(
                gameName = "Reaction Duel",
                score = score.value,
                onSuccess = {
                    println("Reaction Duel score saved successfully!")
                    navController.navigate("home")
                },
                onFailure = { error ->
                    println("Error saving Reaction Duel score: $error")
                    navController.navigate("home")
                }
            )
        } else {
            navController.navigate("home")
        }
    }

    fun resetIcons() {
        coroutineScope.launch {
            delay(2000L)
            isRedIconActive.value = false
            isGreenIconActive.value = false
            feedbackText.value = null
        }
    }

    fun calculateScore(reactionTime: Long): Int {
        return when {
            reactionTime <= 2000 -> 30 // x3 multiplier
            reactionTime <= 4000 -> 20 // x2 multiplier
            else -> 10 // no multiplier
        }
    }

    fun getMaxReactionTime(round: Int): Long {
        return maxOf(1000L, 10000L - (round - 1) * 500L) // Decrease by 0.5 seconds per round, minimum 1 second
    }

    fun getFeedbackText(reactionTime: Long): String {
        return when {
            reactionTime <= 2000 -> "Amazing!"
            reactionTime <= 4000 -> "Fantastic!"
            else -> "Very good!"
        }
    }

    fun startWaitingForSignal() {
        coroutineScope.launch {
            val delayTime = (2000..5000).random()
            delay(delayTime.toLong())
            if (isWaitingForSignal.value && !isGamePaused.value) {
                isWaitingForSignal.value = false
                startTime.value = System.currentTimeMillis()
                backgroundColor.value = Color.Green

                val maxTime = getMaxReactionTime(currentRound.value)
                delay(maxTime)
                if (!isWaitingForSignal.value && !isGamePaused.value) {
                    isRedIconActive.value = true
                    backgroundColor.value = Color.Red
                    lives.value -= 1
                    resetIconsAndRestartRound()
                    if (lives.value <= 0) {
                        showGameOverDialog.value = true
                        isGamePaused.value = true
                    }
                }
            }
        }
    }

    LaunchedEffect(isWaitingForSignal.value, isGamePaused.value) {
        if (isWaitingForSignal.value && !isGamePaused.value) {
            startWaitingForSignal()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Background()

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(20.dp))
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .offset(x = 10.dp)
                        .size(20.dp)
                        .clickable {
                            showExitDialog.value = true
                            isGamePaused.value = true
                        }
                )
                Text(
                    text = "Reaction Duel",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    painter = painterResource(id = R.drawable.reaction_duel),
                    contentDescription = "Game Icon",
                    tint = Color.Unspecified, // Use the default tint
                    modifier = Modifier
                        .offset(x = -(10).dp)
                        .size(25.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Score Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 120.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isRedIconActive.value) R.drawable.red_cross else R.drawable.red_cross_2
                    ),
                    contentDescription = "Red Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .offset(x = -(10).dp)
                        .size(25.dp)
                )
                Text(
                    text = "Points: ${score.value}",
                    style = MyTypography.montserratR.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Icon(
                    painter = painterResource(
                        id = if (isGreenIconActive.value) R.drawable.green_succ else R.drawable.green_succ_2
                    ),
                    contentDescription = "Green Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .offset(x = 10.dp)
                        .size(25.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feedback Text
            feedbackText.value?.let {
                Text(
                    text = it,
                    style = MyTypography.montserratR.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold),
                    color = Color.Yellow,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Reaction Area
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(450.dp)
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(color = backgroundColor.value, shape = RoundedCornerShape(16.dp))
                    .clickable {
                        if (isWaitingForSignal.value) {

                            isRedIconActive.value = true
                            isGreenIconActive.value = false
                            backgroundColor.value = Color.Red
                            score.value -= 5
                            lives.value -= 1
                            isWaitingForSignal.value = true
                            resetIconsAndRestartRound()
                            startWaitingForSignal()
                        } else {

                            val reaction = System.currentTimeMillis() - startTime.value
                            val maxTime = getMaxReactionTime(currentRound.value)

                            if (reaction <= maxTime) {
                                val roundScore = calculateScore(reaction)
                                feedbackText.value = getFeedbackText(reaction)
                                score.value += roundScore
                                isGreenIconActive.value = true
                                isRedIconActive.value = false
                                backgroundColor.value = Color.Gray
                                isWaitingForSignal.value = true
                                currentRound.value += 1
                                resetIcons()
                            } else {

                                isRedIconActive.value = true
                                isGreenIconActive.value = false
                                score.value -= 5
                                lives.value -= 1
                                resetIconsAndRestartRound()
                                if (lives.value <= 0) {
                                    showGameOverDialog.value = true
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {}

            // Lives and Round Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 10.dp)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart),
                            contentDescription = "Life ${index + 1}",
                            tint = if (index < lives.value) Color.Red else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "Round: ${currentRound.value}",
                    style = MyTypography.montserratR.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            // Game Over Dialog
            if (showGameOverDialog.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text(text = "Game Over!", style = MyTypography.montserratR.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)) },
                    text = { Text(text = "You've earned ${score.value} points!", style = MyTypography.montserratR.copy(fontSize = 13.sp)) },
                    confirmButton = {
                        TextButton(onClick = { handleGameOver() }, Modifier.offset(y = 10.dp)) {
                            Text("Back Home", fontSize = 15.sp)
                        }
                    },
                    dismissButton = {}
                )
            }

            // Exit Confirmation Dialog
            if (showExitDialog.value) {
                AlertDialog(
                    modifier = Modifier
                        .height(300.dp)
                        .width(500.dp),
                    onDismissRequest = { showExitDialog.value = false },
                    title = { Text(text = "Are you sure?", style = MyTypography.montserratSB.copy(fontSize = 35.sp)) },
                    text = { Text(text = "You will lose all your points!", style = MyTypography.montserratR.copy(fontSize = 35.sp), lineHeight = 38.sp) },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog.value = false
                            navController.navigate("home")
                        }, Modifier.offset(y= 40.dp)) {
                            Text("Yes", fontSize = 30.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showExitDialog.value = false
                            isGamePaused.value = false
                        }, Modifier.offset(y= 40.dp)) {
                            Text("No", fontSize = 30.sp)
                        }
                    }
                )
            }
        }
    }
}




