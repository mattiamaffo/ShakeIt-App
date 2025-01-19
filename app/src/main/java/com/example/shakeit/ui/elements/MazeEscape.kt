package com.example.shakeit.ui.elements


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.ui.theme.MyTypography
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.shakeit.data.domain.AuthRepository

fun moveBall(
    direction: String,
    ballPosition: MutableState<Pair<Int, Int>>,
    maze: Maze,
    score: MutableState<Int>,
    remainingTime: MutableState<Int>,
    gameRunning: MutableState<Boolean>
) {
    val (row, col) = ballPosition.value
    when (direction) {
        "UP" -> if (row > 0 && !maze.grid[row][col].hasNorthWall) {
            ballPosition.value = Pair(row - 1, col)
        }
        "DOWN" -> if (row < maze.rows - 1 && !maze.grid[row][col].hasSouthWall) {
            ballPosition.value = Pair(row + 1, col)
        }
        "LEFT" -> if (col > 0 && !maze.grid[row][col].hasWestWall) {
            ballPosition.value = Pair(row, col - 1)
        }
        "RIGHT" -> if (col < maze.cols - 1 && !maze.grid[row][col].hasEastWall) {
            ballPosition.value = Pair(row, col + 1)
        }
    }

    // Check for special cells
    val (newRow, newCol) = ballPosition.value
    val currentCell = maze.grid[newRow][newCol]

    if (currentCell.isExtraPoint) {
        score.value += 10 // Add 10 points
        currentCell.isExtraPoint = false // Reset the cell
    }

    if (currentCell.isBomb) {
        score.value -= 2 // Subtract 2 points
        currentCell.isBomb = false // Reset the cell
    }

    if (currentCell.isEnd) {
        // Update score with multiplier
        score.value += 50 + remainingTime.value // Example multiplier
        currentCell.isEnd = false // Reset the cell (optional)
        gameRunning.value = false // Stop the game
    }

    Log.d("BallPosition", "Row: $newRow, Col: $newCol, Score: ${score.value}")
}





@Composable
fun MazeEscapeScreen(navController: NavController, seed: Long = System.currentTimeMillis()) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val authRepository = remember { AuthRepository() }

    val rows = 15
    val cols = 15

    val maze = remember(seed) { generateMaze(rows, cols, seed) }
    val ballPosition = remember { mutableStateOf(Pair(0, 0)) }
    val score = remember { mutableStateOf(0) }
    val gameRunning = remember { mutableStateOf(true) }
    val remainingTime = remember { mutableStateOf(180) }
    val showDialog = remember { mutableStateOf(false) } // To control the alert dialog

    val debounceTime = 200L // Delay in milliseconds
    var lastMoveTime by remember { mutableStateOf(0L) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && gameRunning.value) {
                    val currentTime = System.currentTimeMillis()

                    // Ensure debounce time has passed
                    if (currentTime - lastMoveTime < debounceTime) return

                    val x = event.values[0]
                    val y = event.values[1]

                    val direction = when {
                        x < -3 -> "RIGHT"
                        x > 3 -> "LEFT"
                        y > 3 -> "DOWN"
                        y < -3 -> "UP"
                        else -> null
                    }

                    direction?.let {
                        moveBall(it, ballPosition, maze, score, remainingTime, gameRunning)
                        lastMoveTime = currentTime // Update the last move time
                        if (!gameRunning.value) {
                            showDialog.value = true
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }


    // Timer Logic
    LaunchedEffect(gameRunning.value) {
        while (gameRunning.value && remainingTime.value > 0) {
            delay(1000L)
            remainingTime.value -= 1
        }
    }

    // Format Timer
    val formattedTime = remember(remainingTime.value) {
        val minutes = remainingTime.value / 60
        val seconds = remainingTime.value % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Composable
        Background()

        // Main Column Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Padding for all content
        ) {
            // Top Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Optional padding at the bottom of the header
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )

                    Text(
                        text = "Tilt Maze Escape",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.maze_escape),
                        contentDescription = "Game Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            // Timer Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFE5A000), shape = RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_timer),
                        contentDescription = "Timer Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedTime,
                        style = MyTypography.montserratSB.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            // Maze Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(350.dp)
                        .height(480.dp)
                        .background(Color(0xFFFFD78F), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DrawMaze(maze, ballPosition.value) // Maze inside the beige container
                }
            }

            // Footer Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(Color(0xFF1A1A3C), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (gameRunning.value) R.drawable.ic_pause2 else R.drawable.ic_resume
                            ),
                            contentDescription = if (gameRunning.value) "Pause" else "Resume",
                            tint = Color(0xFFE5A000),
                            modifier = Modifier
                                .size(45.dp)
                                .clickable {
                                    gameRunning.value = !gameRunning.value // game state
                                }
                        )

                        // Score Text
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFF00FFFF), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = score.value.toString(),
                                style = MyTypography.montserratSB.copy(
                                    fontSize = 24.sp,
                                    color = Color.Black
                                )
                            )
                        }

                        // Restart Icon
                        Icon(
                            painter = painterResource(id = R.drawable.ic_restart),
                            contentDescription = "Restart",
                            tint = Color(0xFFE5A000),
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    score.value = 0
                                    remainingTime.value = 240 // Reset timer to 4 minutes
                                }
                        )
                    }
                }
            }

            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = {  },
                    title = { Text("Game Over", style = MyTypography.montserratSB) },
                    text = {
                        Text("Your score: ${score.value}\nRemaining Time Bonus: ${remainingTime.value * 2}", style = MyTypography.montserratSBi)
                    },
                    confirmButton = {
                        Button(onClick = {
                            authRepository.updateMinigameScore(
                                gameName = "Maze Escape",
                                score = score.value,
                                onSuccess = {
                                    println("Maze Escape score updated!")
                                    navController.popBackStack() // Go back to the home screen
                                },
                                onFailure = { error ->
                                    println("Error updating score: $error")
                                    navController.popBackStack()
                                }
                            )
                        }) {
                            Text("Return to Home", style = MyTypography.montserratR)
                        }
                    }
                )
            }
        }
    }
}

fun scaleBitmap(context: Context, resourceId: Int, width: Int, height: Int): ImageBitmap {
    val originalBitmap = BitmapFactory.decodeResource(context.resources, resourceId)
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
    return scaledBitmap.asImageBitmap()
}


@Composable
fun DrawMaze(maze: Maze, ballPosition: Pair<Int, Int>) {
    val context = LocalContext.current

    val starIcon = scaleBitmap(context, R.drawable.ic_star, 40, 40)
    val dollarIcon = scaleBitmap(context, R.drawable.ic_dollar, 40, 40)
    val bombIcon = scaleBitmap(context, R.drawable.ic_bomb, 35, 35)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width / maze.cols
        val cellHeight = size.height / maze.rows

        maze.grid.forEachIndexed { row, cells ->
            cells.forEachIndexed { col, cell ->
                val left = col * cellWidth
                val top = row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                val center = Offset((left + right) / 2, (top + bottom) / 2)

                // Draw walls
                if (cell.hasNorthWall) drawLine(Color.Black, Offset(left, top), Offset(right, top), 4f)
                if (cell.hasSouthWall) drawLine(Color.Black, Offset(left, bottom), Offset(right, bottom), 4f)
                if (cell.hasWestWall) drawLine(Color.Black, Offset(left, top), Offset(left, bottom), 4f)
                if (cell.hasEastWall) drawLine(Color.Black, Offset(right, top), Offset(right, bottom), 4f)

                // Draw special elements
                when {
                    cell.isEnd -> drawImage(starIcon, Offset(center.x - starIcon.width / 2, center.y - starIcon.height / 2))
                    cell.isExtraPoint -> drawImage(dollarIcon, Offset(center.x - dollarIcon.width / 2, center.y - dollarIcon.height / 2))
                    cell.isBomb -> drawImage(bombIcon, Offset(center.x - bombIcon.width / 2, center.y - bombIcon.height / 2))
                }

                // Draw ball
                if (ballPosition == Pair(row, col)) {
                    drawCircle(
                        color = Color.Red,
                        center = center,
                        radius = (cellWidth / 6).toFloat()
                    )
                }
            }
        }
    }
}




// Data class to represent a cell in the maze
data class Cell(
    var hasNorthWall: Boolean = true,
    var hasSouthWall: Boolean = true,
    var hasEastWall: Boolean = true,
    var hasWestWall: Boolean = true,
    var isStart: Boolean = false, // Starting point
    var isEnd: Boolean = false,  // End point with star
    var isExtraPoint: Boolean = false, // Extra point object
    var isBomb: Boolean = false // Bomb
)

// Class to represent the maze
class Maze(val rows: Int, val cols: Int) {
    val grid: Array<Array<Cell>> = Array(rows) { Array(cols) { Cell() } }
}

// Function to generate a random maze with additional elements
fun generateMaze(rows: Int, cols: Int, seed: Long): Maze {
    val maze = Maze(rows, cols)
    val random = Random(seed) // Use the seed for consistent randomization
    val visited = Array(rows) { BooleanArray(cols) }
    val stack = mutableListOf<Pair<Int, Int>>()

    // Initial position
    val startRow = 0
    val startCol = 0
    stack.add(Pair(startRow, startCol))
    visited[startRow][startCol] = true

    val directions = listOf(
        Pair(-1, 0), // North
        Pair(1, 0),  // South
        Pair(0, -1), // West
        Pair(0, 1)   // East
    )

    // Generate the maze using DFS
    while (stack.isNotEmpty()) {
        val current = stack.last()
        val (row, col) = current
        val neighbors = directions.map { Pair(row + it.first, col + it.second) }
            .filter { (r, c) ->
                r in 0 until rows && c in 0 until cols && !visited[r][c]
            }

        if (neighbors.isNotEmpty()) {
            val (nextRow, nextCol) = neighbors.random(random)

            // Remove walls between cells
            if (nextRow < row) {
                maze.grid[row][col].hasNorthWall = false
                maze.grid[nextRow][nextCol].hasSouthWall = false
            } else if (nextRow > row) {
                maze.grid[row][col].hasSouthWall = false
                maze.grid[nextRow][nextCol].hasNorthWall = false
            } else if (nextCol < col) {
                maze.grid[row][col].hasWestWall = false
                maze.grid[nextRow][nextCol].hasEastWall = false
            } else if (nextCol > col) {
                maze.grid[row][col].hasEastWall = false
                maze.grid[nextRow][nextCol].hasWestWall = false
            }

            stack.add(Pair(nextRow, nextCol))
            visited[nextRow][nextCol] = true
        } else {
            stack.removeAt(stack.size - 1)
        }
    }

    // Add special elements
    addSpecialElements(maze, random)

    return maze
}

// Update the special elements function to use the same Random instance
fun addSpecialElements(maze: Maze, random: Random) {
    val rows = maze.rows
    val cols = maze.cols

    // Add the starting point
    val startCell = Pair(0, 0)
    maze.grid[startCell.first][startCell.second].isStart = true

    // Add the ending point with a star (ensure it's reachable)
    val endCell = Pair(rows - 1, cols - 1)
    maze.grid[endCell.first][endCell.second].isEnd = true

    // Add 3 extra-point objects
    repeat(3) {
        var row: Int
        var col: Int
        do {
            row = random.nextInt(rows)
            col = random.nextInt(cols)
        } while (maze.grid[row][col].isStart || maze.grid[row][col].isEnd || maze.grid[row][col].isExtraPoint || maze.grid[row][col].isBomb)
        maze.grid[row][col].isExtraPoint = true
    }

    // Add 2 bombs
    repeat(2) {
        var row: Int
        var col: Int
        do {
            row = random.nextInt(rows)
            col = random.nextInt(cols)
        } while (maze.grid[row][col].isStart || maze.grid[row][col].isEnd || maze.grid[row][col].isExtraPoint || maze.grid[row][col].isBomb)
        maze.grid[row][col].isBomb = true
    }
}


