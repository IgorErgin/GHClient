package com.ergin.ghclient.feature.profile.impl.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.feature.profile.domain.model.UserActivity
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun ProfileContributionMatrix(
    activities: List<UserActivity>,
    modifier: Modifier = Modifier
) {
    val totalEvents = activities.size
    val totalPushEvents = activities.count { it.type == "PushEvent" }

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
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "РАСПРЕДЕЛЕНИЕ ТРУДОДНЕЙ (АКТИВНОСТЬ)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "[ СОБЫТИЙ: $totalEvents ]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Phosphor Pixel Raster Grid calculated from real activity events
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // 16 Columns x 7 Rows Matrix
                    for (col in 0..15) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            for (row in 0..6) {
                                val slotIndex = col * 7 + row
                                val hasActivity = slotIndex < activities.size
                                val isPush = hasActivity && activities[slotIndex].type == "PushEvent"

                                val color = when {
                                    isPush -> MaterialTheme.colorScheme.primary
                                    hasActivity -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(color)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ПУШИ / КОММИТЫ: $totalPushEvents",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.3f)))
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)))
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary))
                }

                Text(
                    text = "ПЕРЕВЫПОЛНЕНИЕ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileContributionMatrixPreview() {
    GHClientTheme {
        ProfileContributionMatrix(activities = emptyList())
    }
}
