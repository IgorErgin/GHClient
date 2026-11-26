package com.ergin.ghclient.feature.profile.impl.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PowerSettingsNew
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ergin.ghclient.core.ui.components.phosphorGlow
import com.ergin.ghclient.ui.theme.GHClientTheme

@Composable
fun ProfileLogoutSection(
    isSafetyUnlocked: Boolean,
    onSafetyUnlockToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
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
                        imageVector = Icons.Default.LockReset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ПРЕДОХРАНИТЕЛЬНЫЙ КЛАПАН СЕССИИ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = if (isSafetyUnlocked) "РЕЖИМ 0-OFF [UNLOCKED]" else "РЕЖИМ 0-OFF [LOCKED]",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSafetyUnlockToggle,
                    shape = CutCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSafetyUnlocked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (isSafetyUnlocked) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.tertiary
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isSafetyUnlocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSafetyUnlocked) "БЛОКИРОВАН" else "РАЗБЛОКИРОВАТЬ",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Button(
                    onClick = onLogoutClick,
                    enabled = isSafetyUnlocked,
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (isSafetyUnlocked) {
                                Modifier.phosphorGlow(glowColor = MaterialTheme.colorScheme.error, blurRadius = 16f)
                            } else {
                                Modifier
                            }
                        ),
                    shape = CutCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                        disabledContentColor = MaterialTheme.colorScheme.outline
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Logout",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "[ ВЫХОД ИЗ СИСТЕМЫ ЭВМ ]",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Locked State")
@Composable
fun ProfileLogoutSectionLockedPreview() {
    GHClientTheme {
        ProfileLogoutSection(
            isSafetyUnlocked = false,
            onSafetyUnlockToggle = {},
            onLogoutClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Unlocked State")
@Composable
fun ProfileLogoutSectionUnlockedPreview() {
    GHClientTheme {
        ProfileLogoutSection(
            isSafetyUnlocked = true,
            onSafetyUnlockToggle = {},
            onLogoutClick = {}
        )
    }
}
