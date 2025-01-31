package com.example.shakeit.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.shakeit.R
import com.example.shakeit.data.domain.AuthRepository
import com.example.shakeit.ui.theme.MyTypography
import com.example.shakeit.ui.theme.Typography
import kotlinx.coroutines.delay

@Composable
fun RegisterPage(
    navController: NavController
) {
    val authRepository = AuthRepository()

    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val phoneNumberState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val showSuccessDialog = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Background
        Background()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Back Button
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back to Login",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Start)
                    .offset(y = 0.dp)
                    .size(25.dp)
                    .clickable { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(0.dp))

            // Logo and Title
            Logo(size = 150.dp)

            Text(
                text = "Create account",
                style = MyTypography.montserratSB,
                fontSize = 35.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Already have an account? Login",
                style = MyTypography.montserratR,
                textDecoration = TextDecoration.Underline,
                fontSize = 10.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { navController.navigate("login") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Register Form
            RegisterForm(
                nameState = nameState,
                emailState = emailState,
                phoneNumberState = phoneNumberState,
                passwordState = passwordState
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Register Button
            CustomButton(
                text = "Register",
                width = 200,
                height = 50,
                onClick = {
                    authRepository.registerUser(
                        email = emailState.value,
                        password = passwordState.value,
                        username = nameState.value,
                        phoneNumber = phoneNumberState.value,
                        onSuccess = {
                            showSuccessDialog.value = true
                        },
                        onFailure = { error ->
                            errorMessage.value = error
                            showDialog.value = true
                        }
                    )
                },
                fontSize = 23
            )
        }

        // Alert Success Dialog
        if (showSuccessDialog.value) {
            AlertDialog(
                onDismissRequest = { /* auto close */ },
                title = {
                    Text(text = "Success!", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
                },
                text = {
                    Text(
                        text = "You are redirected to the login page!",
                        style = MyTypography.montserratR.copy(fontSize = 16.sp)
                    )
                },
                confirmButton = {},
                dismissButton = {}
            )

            // 3 seconds delay
            LaunchedEffect(Unit) {
                delay(3000)
                showSuccessDialog.value = false
                navController.navigate("login")
            }
        }

        // Error Dialog
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Text(text = "Errore", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
                },
                text = {
                    Text(text = errorMessage.value, style = MyTypography.montserratR.copy(fontSize = 16.sp))
                },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog.value = false }
                    ) {
                        Text(text = "OK", style = MyTypography.montserratSB.copy(fontSize = 20.sp))
                    }
                }
            )
        }
    }
}
