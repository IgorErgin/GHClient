package com.ergin.ghclient.feature.profile.impl.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.core.ui.components.phosphorGlow
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun ProfileExperienceMeter(
    modifier: Modifier = Modifier,
    currentXp: Int = 104,
    maxXp: Int = 125,
    level: Int = 25
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = CutCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "❤ ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "ОПЫТ: $currentXp / $maxXp XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "УРОВЕНЬ $level (СТАЖ 8 ЛЕТ)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Segmented Phosphor Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val totalBars = 20
                val filledBars = (totalBars * (currentXp.toFloat() / maxXp)).toInt()

                for (i in 0 until totalBars) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .then(
                                if (i < filledBars) {
                                    Modifier.phosphorGlow(glowColor = MaterialTheme.colorScheme.primary, blurRadius = 8f)
                                } else {
                                    Modifier
                                }
                            )
                            .background(
                                if (i < filledBars) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileExperienceMeterPreview() {
    GHClientTheme {
        ProfileExperienceMeter()
    }
}
