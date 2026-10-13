package com.ergin.ghclient.feature.repository.impl.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.feature.repository.impl.presentation.contract.RepoTab
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun RepoDetailsTabs(
    selectedTab: RepoTab,
    onTabSelected: (RepoTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        RepoTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val icon: ImageVector = when (tab) {
                RepoTab.README -> Icons.Default.Description
                RepoTab.TREE -> Icons.Default.FolderCopy
                RepoTab.COMMITS -> Icons.Default.History
                RepoTab.RELEASES -> Icons.Default.Verified
            }

            Button(
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f),
                shape = CutCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = tab.label,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = " ${tab.label}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RepoDetailsTabsPreview() {
    GHClientTheme {
        RepoDetailsTabs(selectedTab = RepoTab.README, onTabSelected = {})
    }
}
