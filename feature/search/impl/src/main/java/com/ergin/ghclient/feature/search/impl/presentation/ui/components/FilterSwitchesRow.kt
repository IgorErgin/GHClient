package com.ergin.ghclient.feature.search.impl.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.core.ui.components.phosphorGlow
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun FilterSwitchesRow(
    availableLanguages: List<String>,
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "[ ФИЛЬТРАЦИЯ ПО ЯЗЫКАМ / ТИПАМ ]",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "УЗЛОВ: ${availableLanguages.size - 1}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableLanguages.forEach { lang ->
                val isSelected = if (lang == "ВСЕ") selectedLanguage == null else selectedLanguage == lang
                Button(
                    onClick = {
                        if (lang == "ВСЕ") onLanguageSelected(null) else onLanguageSelected(lang)
                    },
                    modifier = if (isSelected) {
                        Modifier.phosphorGlow(glowColor = MaterialTheme.colorScheme.primary, blurRadius = 18f)
                    } else {
                        Modifier
                    },
                    shape = CutCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isSelected) "[■] $lang" else "[ ] $lang",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "All Selected")
@Composable
fun FilterSwitchesRowAllPreview() {
    GHClientTheme {
        FilterSwitchesRow(
            availableLanguages = listOf("ВСЕ", "KOTLIN", "RUST", "TYPESCRIPT", "C/ASM"),
            selectedLanguage = null,
            onLanguageSelected = {}
        )
    }
}

@Preview(showBackground = true, name = "Kotlin Selected")
@Composable
fun FilterSwitchesRowKotlinPreview() {
    GHClientTheme {
        FilterSwitchesRow(
            availableLanguages = listOf("ВСЕ", "KOTLIN", "RUST", "TYPESCRIPT", "C/ASM"),
            selectedLanguage = "KOTLIN",
            onLanguageSelected = {}
        )
    }
}
