package com.ergin.ghclient.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ergin.ghclient.ui.theme.GHClientTheme

/**
 * Оверлей эффекта старого CRT (лампового) монитора:
 * - Горизонтальные бегущие и статические сканирующие линии (Scanlines)
 * - Фосфорное затемнение по краям (Виньетирование выпуклого стекла)
 */
@Composable
fun CrtScreenOverlay(
    modifier: Modifier = Modifier,
    enableScanlines: Boolean = true,
    enableVignette: Boolean = true,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crtScanline")
    val scanlineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanlineOffset"
    )

    Box(modifier = modifier.fillMaxSize()) {
        content()

        if (enableScanlines || enableVignette) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // 1. Горизонтальные сканирующие линии (Scanlines)
                if (enableScanlines) {
                    val spacing = 8f
                    var y = (scanlineOffset % spacing)
                    while (y < height) {
                        drawLine(
                            color = Color.Black.copy(alpha = 0.18f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 2f
                        )
                        y += spacing
                    }
                }

                // 2. Выпуклое стекло CRT-монитора (Виньетка)
                if (enableVignette) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.55f)
                            ),
                            center = Offset(width / 2f, height / 2f),
                            radius = maxOf(width, height) * 0.8f
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CrtScreenOverlayPreview() {
    GHClientTheme {
        CrtScreenOverlay {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
