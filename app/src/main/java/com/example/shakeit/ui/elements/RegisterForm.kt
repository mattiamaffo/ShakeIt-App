package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shakeit.R
import com.example.shakeit.ui.theme.MyTypography

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction

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
            .padding(horizontal = 32.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Name
        OutlinedTextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            placeholder = { Text("Username*", style = MyTypography.montserratR.copy(fontSize = 17.sp, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Name Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 17.sp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            singleLine = true
        )

        // Email
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            placeholder = { Text("Email*", style = MyTypography.montserratR.copy(fontSize = 17.sp, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 17.sp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            singleLine = true
        )

        // Cellular Number
        OutlinedTextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            placeholder = { Text("Phone Number", style = MyTypography.montserratR.copy(fontSize = 17.sp, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_phone),
                    contentDescription = "Phone Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 17.sp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF6C63FF),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            singleLine = true
        )

        // Password
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = { Text("Password*", style = MyTypography.montserratR.copy(fontSize = 17.sp, color = Color.Gray)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock2),
                    contentDescription = "Password Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            textStyle = MyTypography.montserratR.copy(fontSize = 17.sp),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
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
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
            }),
            singleLine = true
        )
    }
}


