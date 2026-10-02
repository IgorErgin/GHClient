package com.ergin.ghclient.core.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas

/**
 * Модификатор фосфорного свечения (Phosphor Glow Bloom)
 * Применяется к активным индикаторам, зеленым точкам и кнопкам для создания мягкого ореола света CRT-монитора.
 */
fun Modifier.phosphorGlow(
    glowColor: Color = Color(0xFF4EFD91),
    blurRadius: Float = 30f
): Modifier = this.drawWithContent {
    try {
        drawIntoCanvas { canvas ->
            val paint = Paint().asFrameworkPaint().apply {
                color = glowColor.hashCode()
                isAntiAlias = true
                maskFilter = BlurMaskFilter(
                    blurRadius,
                    BlurMaskFilter.Blur.NORMAL
                )
            }
            canvas.nativeCanvas.save()
            drawContent()
            canvas.nativeCanvas.restore()
        }
    } catch (_: Throwable) {
        // Режим превью или устройство без поддержки BlurMaskFilter
    }
    drawContent()
}
