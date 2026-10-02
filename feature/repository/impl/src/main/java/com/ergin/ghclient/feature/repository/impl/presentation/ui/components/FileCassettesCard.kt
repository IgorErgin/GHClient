package com.ergin.ghclient.feature.repository.impl.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
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
import com.ergin.ghclient.core.domain.model.Sha
import com.ergin.ghclient.feature.repository.domain.model.FileNode
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun FileCassettesCard(
    fileNodes: List<FileNode>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        shape = CutCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ФАЙЛОВЫЕ КАССЕТЫ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = "[ ВСЕГО: ${fileNodes.size} ]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (fileNodes.isEmpty()) {
                    Text(
                        text = "Кассеты корневого каталога не загружены или пусты",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    fileNodes.take(8).forEach { node ->
                        FileCassetteRow(
                            name = node.name,
                            isFolder = node.type == FileNode.FileType.DIRECTORY,
                            sizeBytes = node.size
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileCassetteRow(
    name: String,
    isFolder: Boolean,
    sizeBytes: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (isFolder) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (isFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = if (isFolder) "<DIR>" else "${sizeBytes / 1024} KB",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true, name = "File Cassettes - Empty State")
@Composable
fun FileCassettesCardEmptyPreview() {
    GHClientTheme {
        FileCassettesCard(fileNodes = emptyList())
    }
}

@Preview(showBackground = true, name = "File Cassettes - Filled State")
@Composable
fun FileCassettesCardFilledPreview() {
    GHClientTheme {
        FileCassettesCard(
            fileNodes = listOf(
                FileNode("compiler/backend", "compiler/backend", FileNode.FileType.DIRECTORY, 0, null, Sha("abc")),
                FileNode("libraries/stdlib", "libraries/stdlib", FileNode.FileType.DIRECTORY, 0, null, Sha("def")),
                FileNode("build.gradle.kts", "build.gradle.kts", FileNode.FileType.FILE, 10240, null, Sha("ghi"))
            )
        )
    }
}
