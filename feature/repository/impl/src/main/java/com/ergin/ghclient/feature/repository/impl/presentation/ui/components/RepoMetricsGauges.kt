package com.ergin.ghclient.feature.repository.impl.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun RepoMetricsGauges(
    starsCount: Int,
    forksCount: Int,
    issuesCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Gauge 1: Stars with CRT Oscilloscope Sparkline
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = CutCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ЗВЁЗДЫ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Text(
                    text = String.format("%,d", starsCount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                // Mini Sparkline Wave Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(2.dp)
                ) {
                    val waveColor = MaterialTheme.colorScheme.primary
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val path = Path().apply {
                            moveTo(0f, h * 0.75f)
                            lineTo(w * 0.15f, h * 0.7f)
                            lineTo(w * 0.30f, h * 0.8f)
                            lineTo(w * 0.45f, h * 0.5f)
                            lineTo(w * 0.60f, h * 0.6f)
                            lineTo(w * 0.75f, h * 0.3f)
                            lineTo(w * 0.85f, h * 0.4f)
                            lineTo(w, h * 0.1f)
                        }
                        drawPath(path = path, color = waveColor, style = Stroke(width = 2f))
                        drawCircle(color = waveColor, radius = 4f, center = Offset(w, h * 0.1f))
                    }
                }

                Text(
                    text = "+142 ЗА СМЕНУ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Gauge 2: Forks with Hardware Step Gauge
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = CutCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ФОРКИ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Default.CallSplit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Text(
                    text = String.format("%,d", forksCount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Step meter bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (i in 0..5) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (i < 4) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHighest
                                )
                        )
                    }
                }

                Text(
                    text = "КОЭФ: 8.5/10",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Gauge 3: Issues with Priority LED Segments
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = CutCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ЗАДАЧИ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Text(
                    text = String.format("%,d", issuesCount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Priority LED segments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.error),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "14", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onError)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "48", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "70", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Text(
                    text = "КРИТ: 14 [РАД]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepoMetricsGaugesPreview() {
    GHClientTheme {
        RepoMetricsGauges(starsCount = 47820, forksCount = 5618, issuesCount = 132)
    }
}
