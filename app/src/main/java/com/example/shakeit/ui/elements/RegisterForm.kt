package com.example.shakeit.ui.elements

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shakeit.R
import com.example.shakeit.ui.theme.MyTypography
import com.example.shakeit.ui.theme.Pontiac

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.example.shakeit.ui.theme.Montserrat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterForm(
    nameState: MutableState<String>,
    emailState: MutableState<String>,
    phoneNumberState: MutableState<String>,
    passwordState: MutableState<String>
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Campo Nome
        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            placeholder = { Text("Username*", style = TextStyle(fontSize = 12.sp, fontFamily = Montserrat, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Name Icon",
                    modifier = Modifier.size(15.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {

                    keyboardController?.hide()

                }
            ),
            singleLine = true
        )

        // Campo Email
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            placeholder = { Text("Email*", style = TextStyle(fontSize = 12.sp, fontFamily = Montserrat, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(15.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {

                    keyboardController?.hide()

                }
            ),
            singleLine = true
        )

        // Campo Numero di Telefono
        OutlinedTextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            placeholder = { Text("Phone Number", style = TextStyle(fontSize = 12.sp, fontFamily = Montserrat, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Phone Icon",
                    modifier = Modifier.size(15.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {

                    keyboardController?.hide()

                }
            ),
            singleLine = true
        )

        // Campo Password
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = { Text("Password*", style = TextStyle(fontSize = 12.sp, fontFamily = Montserrat, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(15.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 12.sp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            singleLine = true
        )
    }
}


