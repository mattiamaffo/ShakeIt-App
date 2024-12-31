package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.example.shakeit.R
import com.example.shakeit.ui.theme.PurpleAvatar

@Composable
fun Avatar(modifier: Modifier = Modifier, cSize: Int = 45, aSize: Int = 45, avatarRes: Int = R.drawable.avatar) {
    Box(
        modifier = modifier
            .size(cSize.dp)
            .clip(CircleShape)
            .background(PurpleAvatar),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatarRes),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(aSize.dp)
                .clip(CircleShape)
        )
    }
}
