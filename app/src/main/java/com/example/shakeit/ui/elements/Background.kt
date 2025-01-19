package com.example.shakeit.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import com.example.shakeit.R

@Composable
fun Background() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Image
        Image(
            painter = painterResource(id = R.drawable.background_xxxhdpi),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Particles
        ParticleAnimation(
            modifier = Modifier.fillMaxSize(),
            particleGroups = listOf(
                ParticleGroup(
                    count = 50,
                    color = Color(0xFFBB86FC),
                    maxRadius = 4f
                ),
                ParticleGroup(
                    count = 30,
                    color = Color.White,
                    maxRadius = 3f
                )
            ),
            verticalOffset = 150f // Offset
        )
    }
}




