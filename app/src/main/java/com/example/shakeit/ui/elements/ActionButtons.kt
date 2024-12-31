package com.example.shakeit.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shakeit.ui.theme.LightBlue1
import com.example.shakeit.ui.theme.Purple1
import com.example.shakeit.ui.theme.Purple2
import com.example.shakeit.ui.theme.Typography

@Composable
fun ActionButtons(
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 57.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Login
        ElevatedButton(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = Purple1),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f)
                .height(45.dp)
        ) {
            Text(text = "Login", color = Color.White, style = Typography.bodyLarge)
        }

        // Register
        ElevatedButton(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue1),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f)
                .height(45.dp)
        ) {
            Text(text = "Register", color = Purple2, style = Typography.bodyLarge)
        }
    }
}
