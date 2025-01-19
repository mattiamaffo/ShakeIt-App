package com.example.shakeit.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shakeit.ui.theme.Typography
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import com.example.shakeit.R
import com.example.shakeit.ui.theme.MyTypography
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun InputFields(
    onForgotPasswordClick: () -> Unit,
    usernameState: MutableState<String>,
    passwordState: MutableState<String>
)  {
    val passwordVisible = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Username Field
        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            placeholder = { Text(
                text = "Username",
                color = Color.Gray,
                style = MyTypography.montserratR.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light

                )
            ) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.alt_email),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            textStyle = MyTypography.montserratR.copy(
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFFE0E0E0), // Background color
            focusedBorderColor = Color(0xFF4742A7), // Color of the border when the field is selected
            unfocusedBorderColor = Color(0xFFBDBDBD) // Color of the border when the field is not selected
            ),
            shape = RoundedCornerShape(30.dp)
        )

        // Campo Password
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = {
                Text(
                    text = "Password",
                    style = MyTypography.montserratR.copy(
                        fontSize = 20.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Light
                    )
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Password Icon", Modifier.size(20.dp), tint = Color.Black)
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible.value) "Hide Password" else "Show Password"
                    )
                }
            },
            singleLine = true,
            textStyle = MyTypography.montserratR.copy(
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 10.dp)
                .height(60.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF4742A7),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(30.dp),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Forgot Password?",
            color = Color.White,
            style = MyTypography.montserratR.copy(fontSize = 12.sp),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPasswordClick() }
                .padding(end = 10.dp)
        )
    }
}
