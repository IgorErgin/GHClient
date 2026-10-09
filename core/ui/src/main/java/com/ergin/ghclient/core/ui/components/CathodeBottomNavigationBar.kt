package com.ergin.ghclient.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.ui.theme.GHClientTheme

enum class CathodeBottomTab(
    val title: String,
    val icon: ImageVector
) {
    REPOS("[01] РЕПОЗ", Icons.Default.FolderCopy),
    ROM("[02] ПЗУ", Icons.Default.Memory),
    DETAILS("[03] ДЕТАЛИ", Icons.Default.Analytics),
    OPERATOR("[04] ОПЕРАТОР", Icons.Default.Badge)
}

/**
 * Индустриальная нижняя панель навигации Cathode Terminal HUD.
 */
@Composable
fun CathodeBottomNavigationBar(
    selectedTab: CathodeBottomTab,
    onTabSelected: (CathodeBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CathodeBottomTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .phosphorGlow(glowColor = MaterialTheme.colorScheme.primary, blurRadius = 12f)
                        } else {
                            Modifier.background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        }
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 6.dp, horizontal = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CathodeBottomNavigationBarPreview() {
    GHClientTheme {
        CathodeBottomNavigationBar(selectedTab = CathodeBottomTab.REPOS, onTabSelected = {})
    }
}
