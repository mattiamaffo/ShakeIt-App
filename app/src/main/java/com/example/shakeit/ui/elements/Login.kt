package com.example.shakeit.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography

@Composable
fun Login(navController: NavHostController) {

    val authRepository = AuthRepository()

    val isLoading = remember { mutableStateOf(false) }

    val usernameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val showErrorDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Background()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 30.dp)
                .padding(horizontal = 30.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Logo
            Logo(
                size = 200.dp,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1f)
            )

            // Login Form
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputFields(
                    onForgotPasswordClick = {},
                    usernameState = usernameState,
                    passwordState = passwordState
                )

                Spacer(modifier = Modifier.height(10.dp))

                ActionButtons(
                    onLoginClick = {
                        isLoading.value = true
                        authRepository.loginUser(
                            usernameOrEmail = usernameState.value,
                            password = passwordState.value,
                            onSuccess = {
                                isLoading.value = false
                                navController.navigate("home")
                            },
                            onFailure = { error ->
                                isLoading.value = false
                                errorMessage.value = error
                                showErrorDialog.value = true
                            }
                        )
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    }
                )

                // Loading Indicator
                if (isLoading.value) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CircularProgressIndicator(
                        color = Color(0xFFFFFFFF),
                        strokeWidth = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(150.dp))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MyTypography.montserratR.copy(fontSize = 10.sp),
                    color = Color.White
                )
                Text(
                    text = "Sign Up",
                    style = MyTypography.montserratSB.copy(fontSize = 10.sp),
                    color = Color.Yellow,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    }
                )
            }
        }
    }

    // Error Dialog
    if (showErrorDialog.value) {
        AlertDialog(
            onDismissRequest = { showErrorDialog.value = false },
            title = {
                Text(text = "Error!", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
            },
            text = {
                Text(text = errorMessage.value, style = MyTypography.montserratR.copy(fontSize = 16.sp))
            },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog.value = false }
                ) {
                    Text(text = "OK", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
                }
            }
        )
    }
}


