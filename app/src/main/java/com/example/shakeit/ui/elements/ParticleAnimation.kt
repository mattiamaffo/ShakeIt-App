package com.example.shakeit.ui.elements

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun ParticleAnimation(
    modifier: Modifier = Modifier,
    particleGroups: List<ParticleGroup>,
    verticalOffset: Float = 0f // Vertical offset
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    // For each group generate particles
    val particles = remember {
        particleGroups.map { group ->
            generateParticles(
                count = group.count,
                maxRadius = group.maxRadius,
                verticalOffset = verticalOffset
            ).map { it to group.color }
        }.flatten()
    }

    // Animate particles
    particles.forEach { (particle, _) ->
        particle.x = infiniteTransition.animateFloat(
            initialValue = particle.initialX,
            targetValue = particle.initialX + particle.offsetX,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        ).value

        particle.y = infiniteTransition.animateFloat(
            initialValue = particle.initialY,
            targetValue = particle.initialY + particle.offsetY,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = particle.duration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        ).value
    }

    // Draw particles
    Canvas(modifier = modifier) {
        particles.forEach { (particle, color) ->
            drawCircle(
                color = color.copy(alpha = particle.alpha),
                radius = particle.radius,
                center = Offset(particle.x, particle.y)
            )
        }
    }
}

data class ParticleGroup(
    val count: Int,
    val color: Color,
    val maxRadius: Float
)

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val offsetX: Float,
    val offsetY: Float,
    val radius: Float,
    val alpha: Float,
    val duration: Int,
    var x: Float = 0f,
    var y: Float = 0f
)

private fun generateParticles(count: Int, maxRadius: Float = 5f, verticalOffset: Float = 0f): List<Particle> {
    val particles = mutableListOf<Particle>()
    repeat(count) {
        particles.add(
            Particle(
                initialX = Random.nextFloat() * 1080,
                initialY = Random.nextFloat() * 1920 + verticalOffset,
                offsetX = Random.nextFloat() * 100 - 50,
                offsetY = Random.nextFloat() * 100 - 50,
                radius = Random.nextFloat() * maxRadius + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.3f,
                duration = Random.nextInt(2000, 4000)
            )
        )
    }
    return particles
}


