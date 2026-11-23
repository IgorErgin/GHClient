package com.ergin.ghclient.feature.upload.impl.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun UploadForm(
    owner: String,
    repoName: String,
    pathInput: String,
    onPathInputChanged: (String) -> Unit,
    messageInput: String,
    onMessageInputChanged: (String) -> Unit,
    selectedFileName: String?,
    selectedFileSizeKb: Long,
    onSelectFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Target Repo Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            shape = CutCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ЦЕЛЕВОЙ РЕПОЗИТОРИЙ:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "$owner / $repoName",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // File Selection Box
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "[ СЧИТЫВАНИЕ ФАЙЛА С НАКОПИТЕЛЯ ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onSelectFileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = CutCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FolderZip,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (selectedFileName != null) "[ $selectedFileName ($selectedFileSizeKb KB) ]" else "[ 📂 СЧИТАТЬ ДАМП С ПЕРФОЛЕНТЫ ]",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Target Path Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "[ ЦЕЛЕВОЙ ПУТЬ СОХРАНЕНИЯ (PATH) ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = CutCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">_ ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    BasicTextField(
                        value = pathInput,
                        onValueChange = onPathInputChanged,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true
                    )
                }
            }
        }

        // Commit Message Input
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "[ КОММЕНТАРИЙ К КОММИТУ (MESSAGE) ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = CutCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">_ ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    BasicTextField(
                        value = messageInput,
                        onValueChange = onMessageInputChanged,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadFormPreview() {
    GHClientTheme {
        UploadForm(
            owner = "jetbrains",
            repoName = "kotlin",
            pathInput = "docs/spec_v2.md",
            onPathInputChanged = {},
            messageInput = "Добавление документации в реестр",
            onMessageInputChanged = {},
            selectedFileName = "spec.md",
            selectedFileSizeKb = 12,
            onSelectFileClick = {}
        )
    }
}
