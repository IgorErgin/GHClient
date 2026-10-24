package com.ergin.ghclient.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ergin.ghclient.ui.theme.GHClientTheme

/**
 * Полнофункциональный парсер и рендерер GitHub Markdown в стиле Cathode Terminal.
 * Поддерживает:
 * - Полноразмерные изображения `![alt](url)` и HTML `<img src="url">` в кассетных рамках
 * - Ссылочные бейджи статуса `[![alt](img)](link)` -> интерактивные терминальные бейджи
 * - Заголовки `#`, `##`, `###` -> фосфорно-зеленые текстовые блоки
 * - Обычные ссылки `[text](url)` -> форматированный чистый текст
 * - Листинги кода ```` ``` ```` -> блоки терминального кода
 * - Списки `- `, `* ` -> моноширинные маркеры `[•]`
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CathodeMarkdown(
    markdownText: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val cleanText = sanitizeMarkdown(markdownText)
    val lines = cleanText.lines()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        var inCodeBlock = false
        val codeBlockBuffer = StringBuilder()

        for (line in lines) {
            val trimmed = line.trim()

            when {
                trimmed.startsWith("```") -> {
                    if (inCodeBlock) {
                        CodeBlockSnippet(code = codeBlockBuffer.toString().trimEnd())
                        codeBlockBuffer.clear()
                        inCodeBlock = false
                    } else {
                        inCodeBlock = true
                    }
                }
                inCodeBlock -> {
                    codeBlockBuffer.append(line).append("\n")
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.removePrefix("# ").uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.removePrefix("## ").uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### ").uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") -> {
                    val bulletText = trimmed.substring(2)
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)) {
                                append("[•] ")
                            }
                            append(parseMarkdownText(bulletText))
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                isStandaloneImage(trimmed) -> {
                    val (alt, url) = extractSingleImage(trimmed)
                    if (!url.isNullOrBlank()) {
                        CathodeImageCard(
                            imageUrl = url,
                            altText = alt,
                            onClick = {
                                try { uriHandler.openUri(url) } catch (_: Exception) {}
                            }
                        )
                    }
                }
                isBadgeLine(trimmed) -> {
                    val badges = extractBadges(trimmed)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        badges.forEach { badge ->
                            BadgeItem(
                                label = badge.label,
                                imageUrl = badge.imageUrl,
                                onClick = {
                                    badge.linkUrl?.let { url ->
                                        try { uriHandler.openUri(url) } catch (_: Exception) {}
                                    }
                                }
                            )
                        }
                    }
                }
                trimmed.isNotBlank() -> {
                    val formatted = parseMarkdownText(trimmed)
                    if (formatted.isNotBlank()) {
                        Text(
                            text = formatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (inCodeBlock && codeBlockBuffer.isNotEmpty()) {
            CodeBlockSnippet(code = codeBlockBuffer.toString().trimEnd())
        }
    }
}

@Composable
private fun CathodeImageCard(
    imageUrl: String,
    altText: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = CutCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ИЗОБРАЖЕНИЕ МОДУЛЯ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!altText.isNullOrBlank()) {
                    Text(
                        text = altText.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = altText,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(0.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
    }
}

private data class BadgeData(val label: String, val imageUrl: String?, val linkUrl: String?)

private fun isStandaloneImage(line: String): Boolean {
    val imageRegex = Regex("""^!\[(.*?)\]\((.*?)\)$""")
    val htmlImgRegex = Regex("""<img\s+[^>]*src=["'](.*?)["'][^>]*>""")
    return imageRegex.matches(line) || htmlImgRegex.containsMatchIn(line)
}

private fun extractSingleImage(line: String): Pair<String?, String?> {
    val imageRegex = Regex("""!\[(.*?)\]\((.*?)\)""")
    val htmlImgRegex = Regex("""<img\s+[^>]*src=["'](.*?)["'][^>]*>""")

    val match = imageRegex.find(line)
    if (match != null) {
        return Pair(match.groupValues[1].ifBlank { null }, match.groupValues[2])
    }

    val htmlMatch = htmlImgRegex.find(line)
    if (htmlMatch != null) {
        return Pair("ИЗОБРАЖЕНИЕ", htmlMatch.groupValues[1])
    }

    return Pair(null, null)
}

private fun isBadgeLine(line: String): Boolean {
    return line.contains("![") || line.contains("<img")
}

private fun extractBadges(line: String): List<BadgeData> {
    val result = mutableListOf<BadgeData>()
    val imageLinkRegex = Regex("""\[!\[(.*?)\]\((.*?)\)\]\((.*?)\)""")
    val singleImageRegex = Regex("""!\[(.*?)\]\((.*?)\)""")

    imageLinkRegex.findAll(line).forEach { match ->
        val label = match.groupValues[1].ifBlank { "БЕЙДЖ" }
        val imgUrl = match.groupValues[2]
        val linkUrl = match.groupValues[3]
        result.add(BadgeData(label = label.uppercase(), imageUrl = imgUrl, linkUrl = linkUrl))
    }

    if (result.isEmpty()) {
        singleImageRegex.findAll(line).forEach { match ->
            val label = match.groupValues[1].ifBlank { "МЕТРИКА" }
            val imgUrl = match.groupValues[2]
            result.add(BadgeData(label = label.uppercase(), imageUrl = imgUrl, linkUrl = null))
        }
    }

    return result
}

private fun sanitizeMarkdown(rawText: String): String {
    return rawText
        .replace(Regex("""<p[^>]*>"""), "")
        .replace("</p>", "")
        .replace("<div[^>]*>", "")
        .replace("</div>", "")
        .replace("&nbsp;", " ")
}

private fun parseMarkdownText(text: String): String {
    var clean = text
    clean = clean.replace(Regex("""\[!\[(.*?)\]\((.*?)\)\]\((.*?)\)"""), "")
    clean = clean.replace(Regex("""!\[(.*?)\]\((.*?)\)"""), "")
    clean = clean.replace(Regex("""\[(.*?)\]\((.*?)\)""")) { match ->
        match.groupValues[1]
    }
    clean = clean.replace("**", "").replace("`", "")
    return clean.trim()
}

@Composable
private fun BadgeItem(
    label: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    modifier = Modifier.heightIn(max = 18.dp)
                )
            }
            Text(
                text = "[ $label ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun CodeBlockSnippet(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(8.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CathodeMarkdownPreview() {
    GHClientTheme {
        CathodeMarkdown(
            markdownText = """
                # Архитектура Проекта
                ![Схема архитектуры](https://raw.githubusercontent.com/jetbrains/kotlin/master/license/logo.png)
                
                [![official project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com)
            """.trimIndent()
        )
    }
}
