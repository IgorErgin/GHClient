package com.ergin.ghclient.core.ui.components

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.ui.theme.GHClientTheme
import org.intellij.lang.annotations.Language

@Language("AGSL")
private val CRT_BARREL_AGSL = """
    uniform shader content;
    uniform vec2 size;
    uniform float distortion;
    uniform float chromatic;

    vec4 main(vec2 coord) {
        vec2 uv = (coord / size) - 0.5;
        float r2 = uv.x * uv.x + uv.y * uv.y;
        
        // Регулируемое искажение линзы (Barrel Distortion)
        uv *= 1.0 + distortion * r2;
        uv += 0.5;
        
        // Граница маски безеля кинескопа
        if (uv.x < 0.005 || uv.x > 0.995 || uv.y < 0.005 || uv.y > 0.995) {
            return vec4(0.0, 0.0, 0.0, 1.0);
        }
        
        // Регулируемая хроматическая аберрация (сдвиг каналов)
        vec2 rOffset = (uv - 0.5) * (chromatic * 0.08);
        vec2 bOffset = (uv - 0.5) * -(chromatic * 0.08);
        
        float r = content.eval((uv + rOffset) * size).r;
        float g = content.eval(uv * size).g;
        float b = content.eval((uv + bOffset) * size).b;
        float a = content.eval(uv * size).a;
        
        return vec4(r, g, b, a);
    }
""".trimIndent()

/**
 * Выпуклая геометрическая форма CRT-экрана (Barrel Lens Convex Shape)
 */
class CrtBarrelShape(private val curveAmountDp: Float = 16f) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val curvePx = with(density) { curveAmountDp.dp.toPx() }
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(curvePx, curvePx)
            quadraticTo(width / 2f, -curvePx * 0.5f, width - curvePx, curvePx)
            quadraticTo(width + curvePx * 0.5f, height / 2f, width - curvePx, height - curvePx)
            quadraticTo(width / 2f, height + curvePx * 0.5f, curvePx, height - curvePx)
            quadraticTo(-curvePx * 0.5f, height / 2f, curvePx, curvePx)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Оверлей CRT-монитора с параметрами регулировки степени искажений:
 * @param distortionIntensity Степень выпуклости линзы кинескопа (рекомендуется 0.0f..0.5f)
 * @param chromaticAberrationIntensity Сила хроматической аберрации (RGB сдвиг)
 * @param scanlineOpacity Прозрачность бегущих сканирующих линий (Scanlines)
 * @param vignetteOpacity Прозрачность затеняющей виньетки по краям
 */
@Composable
fun CrtScreenOverlay(
    modifier: Modifier = Modifier,
    enableScanlines: Boolean = true,
    enableVignette: Boolean = true,
    enableChromaticAberration: Boolean = true,
    distortionIntensity: Float = 0.08f,
    chromaticAberrationIntensity: Float = 0.06f,
    scanlineOpacity: Float = 0.18f,
    vignetteOpacity: Float = 0.85f,
    content: @Composable () -> Unit
) {
    val isPreview = LocalInspectionMode.current
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

    // Применение AGSL шейдера на Android 13+ или геометрического CrtBarrelShape (включая режим Preview в Android Studio)
    val barrelShaderModifier = if (!isPreview && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val runtimeShader = remember { RuntimeShader(CRT_BARREL_AGSL) }
        Modifier.graphicsLayer {
            runtimeShader.setFloatUniform("size", size.width, size.height)
            runtimeShader.setFloatUniform("distortion", distortionIntensity)
            runtimeShader.setFloatUniform("chromatic", if (enableChromaticAberration) chromaticAberrationIntensity else 0f)
            try {
                renderEffect = RenderEffect
                    .createRuntimeShaderEffect(runtimeShader, "content")
                    .asComposeRenderEffect()
            } catch (_: Throwable) {}
        }
    } else {
        Modifier
            .scale(1.0f + distortionIntensity * 0.1f)
            .clip(CrtBarrelShape(curveAmountDp = 16f * (distortionIntensity / 0.18f)))
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().then(barrelShaderModifier)) {
            content()
        }

        // Оверлей сферического стекла, бегущих сканирующих линий и каемки
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val curvePx = (16.dp.toPx()) * (distortionIntensity / 0.18f)

            // 1. Параболическая рамка выпуклой колбы кинескопа (Inverted Barrel Bezel)
            val outerBezelPath = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, width, height))

                moveTo(curvePx, curvePx)
                quadraticTo(width / 2f, -curvePx * 0.3f, width - curvePx, curvePx)
                quadraticTo(width + curvePx * 0.3f, height / 2f, width - curvePx, height - curvePx)
                quadraticTo(width / 2f, height + curvePx * 0.3f, curvePx, height - curvePx)
                quadraticTo(-curvePx * 0.3f, height / 2f, curvePx, curvePx)
                close()
            }

            drawPath(
                path = outerBezelPath,
                color = Color.Black.copy(alpha = vignetteOpacity.coerceIn(0f, 1f))
            )

            // 2. Изогнутые сканирующие линии (Scanlines)
            if (enableScanlines) {
                val spacing = 8f
                var y = (scanlineOffset % spacing)
                while (y < height) {
                    val curveOffsetY = (y - height / 2f) / (height / 2f)
                    val arch = (1f - curveOffsetY * curveOffsetY) * (6f * (distortionIntensity / 0.18f))

                    val linePath = Path().apply {
                        moveTo(0f, y)
                        quadraticTo(width / 2f, y + arch, width, y)
                    }
                    drawPath(
                        path = linePath,
                        color = Color.Black.copy(alpha = scanlineOpacity),
                        style = Stroke(width = 2f)
                    )
                    y += spacing
                }
            }

            // 3. Хроматический цветной ореол по дуге выпуклости
            if (enableChromaticAberration) {
                val alphaFringe = (chromaticAberrationIntensity * 3f).coerceIn(0f, 0.8f)

                val redFringePath = Path().apply {
                    moveTo(curvePx - 2f, curvePx)
                    quadraticTo(width / 2f, -curvePx * 0.3f - 2f, width - curvePx + 2f, curvePx)
                }
                drawPath(
                    path = redFringePath,
                    color = Color(0xFFFF0055).copy(alpha = alphaFringe),
                    style = Stroke(width = 3f)
                )

                val cyanFringePath = Path().apply {
                    moveTo(curvePx, height - curvePx + 2f)
                    quadraticTo(width / 2f, height + curvePx * 0.3f + 2f, width - curvePx, height - curvePx + 2f)
                }
                drawPath(
                    path = cyanFringePath,
                    color = Color(0xFF00FFFF).copy(alpha = alphaFringe),
                    style = Stroke(width = 3f)
                )
            }

            // 4. Виньетка выпуклой сферической линзы
            if (enableVignette) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = vignetteOpacity * 0.35f),
                            Color.Black.copy(alpha = vignetteOpacity)
                        ),
                        center = Offset(width / 2f, height / 2f),
                        radius = maxOf(width, height) * 0.62f
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Default Distortion (0.22)")
@Composable
fun CrtScreenOverlayDefaultPreview() {
    GHClientTheme {
        CrtScreenOverlay {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
