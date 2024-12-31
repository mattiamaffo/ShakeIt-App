package com.example.shakeit.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
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
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Campo Username
        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            placeholder = { Text(
                text = "Username",
                style = MyTypography.montserratR.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
            ) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.alt_email),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(24.dp)
                )
            },
            singleLine = true,
            textStyle = MyTypography.montserratR.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color(0xFFE0E0E0), // Background color
            focusedBorderColor = Color(0xFF4742A7), // Colore del bordo quando la casella è selezionata
            unfocusedBorderColor = Color(0xFFBDBDBD) // Colore del bordo quando la casella non è selezionata
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                )
            },
            leadingIcon = {
                Icon(Icons.Filled.Lock, contentDescription = "Password Icon")
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF4742A7),
                unfocusedBorderColor = Color(0xFFBDBDBD)
            ),
            shape = RoundedCornerShape(30.dp),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation()
        )

        Text(
            text = "Forgot Password?",
            color = Color.White,
            style = Typography.bodySmall,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPasswordClick() }
                .padding(end = 10.dp)
        )
    }
}
