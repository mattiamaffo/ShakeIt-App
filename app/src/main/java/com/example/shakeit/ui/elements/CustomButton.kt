package com.example.shakeit.ui.elements

import android.graphics.fonts.FontFamily
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shakeit.ui.theme.LightBlue2
import com.example.shakeit.ui.theme.Pontiac
import com.example.shakeit.ui.theme.Purple2

@Composable
fun CustomButton(
    text: String,
    width: Int = 170,
    height: Int = 40,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Purple2,
    textColor: Color = LightBlue2,
    fontSize: Int = 14,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(fontSize = fontSize.sp, fontFamily = Pontiac, color = textColor, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
        )
    }
}
