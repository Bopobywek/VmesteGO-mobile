package ru.vmestego.ui.mainActivity

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1500,
): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")

    val animatedOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    return drawWithContent {
        drawContent()

        val width = size.width
        val height = size.height

        val x = width * animatedOffset
        val y = height * animatedOffset

        val brush = Brush.radialGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.4f),
                Color.Gray.copy(alpha = 0.6f),
                Color.DarkGray.copy(alpha = 0.4f)
            ),
            center = Offset(x, y),
            radius = maxOf(width, height)
        )

        drawRect(brush = brush)
    }
}