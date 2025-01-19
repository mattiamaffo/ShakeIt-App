package com.example.shakeit.ui.elements

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography
import kotlin.math.sqrt

fun generateRandomSequence(): List<String> {
    return List(3) { listOf("stop", "wave").random() }
}
@Composable
fun ShakeTheBombScreen(navController: NavController) {
    val showExitDialog = remember { mutableStateOf(false) }
    val isGamePaused = remember { mutableStateOf(false) }
    val showGameOverDialog = remember { mutableStateOf(false) }
    val authRepository = remember { AuthRepository() }

    val timeLeft = remember { mutableStateOf(120) }
    val timerRunning = remember { mutableStateOf(true) }
    val isGameOver = remember { mutableStateOf(false) }


    // Initial state
    val sequence = remember { mutableStateOf(generateRandomSequence()) }
    val currentSymbolIndex = remember { mutableStateOf(0) }
    val isSymbolCompleted = remember { mutableStateOf(false) }
    val shakeDetected = remember { mutableStateOf(false) }
    val shakeCount = remember { mutableStateOf(0) }
    val score = remember { mutableStateOf(0) }
    val lastShakeTime = remember { mutableStateOf(0L) }
    val currentRound = remember { mutableStateOf(1) }

    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val shakeThreshold = 20f
    val shakeInterval = 1000L

    // Function to advance to the next symbol
    fun advanceToNextSymbol() {
        if (isGameOver.value) return

        currentSymbolIndex.value++

        if (currentSymbolIndex.value >= sequence.value.size) {
            if (currentRound.value < 3) {
                currentRound.value++
                score.value += 10
                currentSymbolIndex.value = 0
                sequence.value = generateRandomSequence()
                Log.d("GameLog", "Sequence completed. New score: ${score.value}")
                Log.d("GameLog", "CurrentSymbolIndex: ${currentSymbolIndex.value}, SequenceSize: ${sequence.value.size}, IsGameOver: ${isGameOver.value}")
            } else {
                // End of game
                isGameOver.value = true
                showGameOverDialog.value = true
            }
        }
    }


// Sensor Listener
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                    val currentTime = System.currentTimeMillis()

                    if (magnitude > shakeThreshold && currentTime - lastShakeTime.value > shakeInterval) {
                        lastShakeTime.value = currentTime
                        shakeCount.value++
                        shakeDetected.value = true
                        Log.d("GameLog", "Shake detected. Count: ${shakeCount.value}")

                        // Check if the shake is correct for the wave symbol
                        if (sequence.value[currentSymbolIndex.value] == "wave" && shakeCount.value >= 1) {
                            score.value += 5
                            shakeCount.value = 0
                            isSymbolCompleted.value = true
                            Log.d("GameLog", "Wave completed")

                            // Advance to the next symbol
                            advanceToNextSymbol()
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

// Register and unregister sensor listener
    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

// Coroutine to check the sequence
    LaunchedEffect(currentSymbolIndex.value) {
        if (isGameOver.value || currentSymbolIndex.value >= sequence.value.size) return@LaunchedEffect

        when (sequence.value[currentSymbolIndex.value]) {
            "stop" -> {
                shakeDetected.value = false
                val stopDuration = 3000L // 3 seconds
                val startTime = System.currentTimeMillis()

                while (System.currentTimeMillis() - startTime < stopDuration) {
                    delay(100L) // Check every 100ms
                    if (shakeDetected.value) {
                        // Movement detected during stop
                        score.value -= 3
                        currentSymbolIndex.value = 0
                        Log.d("GameLog", "Movement detected during 'stop'. Sequence reset. New score: ${score.value}")
                        return@LaunchedEffect
                    }
                }

                // Correct stop
                score.value += 5
                Log.d("GameLog", "Stop completed")
                advanceToNextSymbol()
            }
        }
    }

    LaunchedEffect(timerRunning.value) {
        if (timerRunning.value) {
            while (timeLeft.value > 0) {
                delay(1000L)
                timeLeft.value--
            }
            timerRunning.value = false
            navController.navigate("home")
            Log.d("GameLog", "Timer finished. Game over.")
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
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
                    text = "Shake the Bomb",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    painter = painterResource(id = R.drawable.shake_the_bomb),
                    contentDescription = "Game Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .offset(x = -(10).dp)
                        .size(35.dp)
                )
            }
            // Central Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(120.dp))
                        .border(width = 3.dp, color = Color(0xFF5C43CC), shape = RoundedCornerShape(120.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.bomb_icon),
                            contentDescription = "Bomb Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(130.dp)
                                .offset(x = 8.dp, y = -(10).dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Will you be able to disarm?",
                            style = MyTypography.montserratSB.copy(fontSize = 18.sp),
                            color = Color(0xFF5C43CC),
                            softWrap = true,
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))

                // Timer, Sequence and Points
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(500.dp)
                            .background(color = Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp))
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "REPEAT THE SEQUENCE!",
                                style = MyTypography.montserratSB.copy(fontSize = 20.sp),
                                color = Color(0xFF5C43CC)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Timer
                            Box(
                                modifier = Modifier
                                    .size(250.dp, 140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.timer_back),
                                    contentDescription = "Timer Background",
                                    modifier = Modifier
                                        .fillMaxSize()
                                )
                                Text(
                                    text = String.format("%02d:%02d", timeLeft.value / 60, timeLeft.value % 60),
                                    style = MyTypography.montserratSB.copy(fontSize = 30.sp),
                                    color = Color.Black
                                )
                            }

                            Text(
                                text = "Don't waste your time...",
                                style = MyTypography.montserratI.copy(fontSize = 20.sp),
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Score
                            Text(
                                text = "POINTS: ${score.value}",
                                style = MyTypography.montserratSB.copy(fontSize = 20.sp),
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(100.dp)
                                    .background(color = Color(0xFF5C43CC), shape = RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    sequence.value.forEachIndexed { index, symbol ->
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(
                                                    color = if (index < currentSymbolIndex.value) Color.Green else Color.LightGray,
                                                    shape = RoundedCornerShape(45.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    id = when (symbol) {
                                                        "stop" -> R.drawable.stop_icon
                                                        "wave" -> R.drawable.shake_icon
                                                        "three_wave" -> R.drawable.double_shake_ic
                                                        else -> R.drawable.placeholder
                                                    }
                                                ),
                                                contentDescription = "$symbol Icon",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(45.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
        if (showGameOverDialog.value) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = "Game Over", style = MyTypography.montserratSB.copy(fontSize = 20.sp)) },
                text = { Text(text = "You've completed all rounds! Your score: ${score.value}", style = MyTypography.montserratR.copy(fontSize = 16.sp)) },
                confirmButton = {
                    TextButton(onClick = {
                        authRepository.updateMinigameScore(
                            gameName = "Shake The Bomb",
                            score = score.value,
                            onSuccess = {
                                println("Shake The Bomb score updated!")
                                navController.navigate("home")
                            },
                            onFailure = { error ->
                                println("Error updating score: $error")
                                navController.navigate("home")
                            }
                        )
                    }) {
                        Text(text = "Return to Home", style = MyTypography.montserratSB.copy(fontSize = 18.sp))
                    }
                }
            )
        }
    }

}

