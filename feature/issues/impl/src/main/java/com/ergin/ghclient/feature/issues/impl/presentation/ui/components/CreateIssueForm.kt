package com.ergin.ghclient.feature.issues.impl.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.core.ui.components.CathodeMarkdown
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun CreateIssueForm(
    owner: String,
    repoName: String,
    titleInput: String,
    onTitleInputChanged: (String) -> Unit,
    bodyInput: String,
    onBodyInputChanged: (String) -> Unit,
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

        // Title Input Field
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "[ ЗАГОЛОВОК РАПОРТА / ISSUE TITLE ]",
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

                    Box(modifier = Modifier.weight(1f)) {
                        if (titleInput.isEmpty()) {
                            Text(
                                text = "ВВЕДИТЕ ЗАГОЛОВОК...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        BasicTextField(
                            value = titleInput,
                            onValueChange = onTitleInputChanged,
                            modifier = Modifier.fillMaxWidth(),
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

        // Body Input Field
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "[ ПОДРОБНОЕ ОПИСАНИЕ СБОЯ / MARKDOWN BODY ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = CutCornerShape(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .padding(10.dp)
                ) {
                    if (bodyInput.isEmpty()) {
                        Text(
                            text = "ПОДДЕРЖИВАЕТСЯ MARKDOWN РАЗМЕТКА (#, ```, - )...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    BasicTextField(
                        value = bodyInput,
                        onValueChange = onBodyInputChanged,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        // Live Markdown Preview Box
        if (bodyInput.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                shape = CutCornerShape(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "ПРЕДВАРИТЕЛЬНЫЙ ПРОСМОТР РАЗМЕТКИ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    CathodeMarkdown(markdownText = bodyInput)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateIssueFormPreview() {
    GHClientTheme {
        CreateIssueForm(
            owner = "jetbrains",
            repoName = "kotlin",
            titleInput = "Сбой компилятора K2",
            onTitleInputChanged = {},
            bodyInput = "# Проблема в K2\n- Шаг 1\n- Шаг 2",
            onBodyInputChanged = {}
        )
    }
}
