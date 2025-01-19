package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shakeit.R

@Composable
fun Logo(size: Dp = 250.dp, padding: Dp = 0.dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_nuovo),
        contentDescription = null,
        modifier = Modifier
            .padding(bottom = padding)
            .size(size) // Size of the logo
    )
}


