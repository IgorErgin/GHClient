package com.ergin.ghclient.feature.auth.impl.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.core.ui.components.phosphorGlow
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun OctocatVectorBox(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = CutCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Reticle Corner Crosshair Registration Marks (+)
            Text(
                text = "+",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = "+",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Text(
                text = "+",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Text(
                text = "+",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "<< ТАКТИЧЕСКИЙ МАСКОТ: OCTOCAT >>",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Monochromatic Vector Octocat Silhouette Canvas
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .phosphorGlow(glowColor = MaterialTheme.colorScheme.primary, blurRadius = 16f),
                    contentAlignment = Alignment.Center
                ) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val darkBg = MaterialTheme.colorScheme.surfaceContainerLowest

                    Canvas(modifier = Modifier.size(72.dp)) {
                        val w = size.width
                        val h = size.height

                        // Cat Ears
                        val leftEar = Path().apply {
                            moveTo(w * 0.2f, h * 0.2f)
                            lineTo(w * 0.32f, h * 0.08f)
                            lineTo(w * 0.38f, h * 0.26f)
                            close()
                        }
                        val rightEar = Path().apply {
                            moveTo(w * 0.8f, h * 0.2f)
                            lineTo(w * 0.68f, h * 0.08f)
                            lineTo(w * 0.62f, h * 0.26f)
                            close()
                        }
                        drawPath(leftEar, primaryColor)
                        drawPath(rightEar, primaryColor)

                        // Head Oval
                        drawCircle(color = primaryColor, radius = w * 0.32f, center = Offset(w * 0.5f, h * 0.38f))

                        // Eyes (Cutouts)
                        drawCircle(color = darkBg, radius = w * 0.06f, center = Offset(w * 0.4f, h * 0.36f))
                        drawCircle(color = darkBg, radius = w * 0.06f, center = Offset(w * 0.6f, h * 0.36f))
                        drawCircle(color = primaryColor, radius = w * 0.025f, center = Offset(w * 0.41f, h * 0.35f))
                        drawCircle(color = primaryColor, radius = w * 0.025f, center = Offset(w * 0.61f, h * 0.35f))

                        // Torso
                        drawRect(color = primaryColor, topLeft = Offset(w * 0.38f, h * 0.58f), size = Size(w * 0.24f, h * 0.22f))
                    }
                }

                // Segmented Phosphor Gauge Matrix
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.height(12.dp)
                ) {
                    for (i in 0..9) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(
                                    when {
                                        i < 6 -> MaterialTheme.colorScheme.primary
                                        i < 8 -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        )
                    }
                }

                Text(
                    text = "[ СИНХРОНИЗАЦИЯ: 80% РАБОЧИЙ РЕЖИМ ]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OctocatVectorBoxPreview() {
    GHClientTheme {
        OctocatVectorBox()
    }
}
