package com.ergin.ghclient.feature.auth.impl.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ergin.ghclient.core.ui.components.CrtScreenOverlay
import com.ergin.ghclient.core.ui.components.ErrorView
import com.ergin.ghclient.core.ui.components.LoadingView
import com.ergin.ghclient.feature.auth.impl.presentation.AuthViewModel
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthEffect
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthEvent
import com.ergin.ghclient.feature.auth.impl.presentation.contract.AuthState
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.AuthBottomTelemetryStrip
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.AuthHeaderStatusLine
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.AuthInputControls
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.AuthTopChassisBezel
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.DiagnosticBootLog
import com.ergin.ghclient.feature.auth.impl.presentation.ui.components.OctocatVectorBox
import com.ergin.ghclient.ui.theme.GHClientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthRoute(
    onNavigateToHome: () -> Unit,
    oauthCode: String? = null,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(oauthCode) {
        if (!oauthCode.isNullOrBlank()) {
            viewModel.onEvent(AuthEvent.OAuthCodeReceived(oauthCode))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AuthEffect.NavigateToHome -> onNavigateToHome()
                is AuthEffect.OpenOAuthWebPage -> {
                    try {
                        val customTabsIntent = CustomTabsIntent.Builder().build()
                        customTabsIntent.launchUrl(context, Uri.parse(effect.url))
                    } catch (_: Exception) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                }
                is AuthEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    AuthScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun AuthScreen(
    uiState: AuthState,
    onEvent: (AuthEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CrtScreenOverlay(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Industrial Top Chassis Bezel
                item {
                    AuthTopChassisBezel()
                }

                // Header Screen Status Line
                item {
                    AuthHeaderStatusLine()
                }

                // Octocat Reticle Vector Box
                item {
                    OctocatVectorBox()
                }

                // Diagnostic Boot Log Stream
                item {
                    DiagnosticBootLog()
                }

                when {
                    uiState.isLoading -> {
                        item {
                            LoadingView()
                        }
                    }
                    uiState.errorMessage != null -> {
                        item {
                            ErrorView(
                                message = uiState.errorMessage,
                                onRetry = { onEvent(AuthEvent.SubmitTokenClicked) }
                            )
                        }
                    }
                    else -> {
                        // Input Controls
                        item {
                            AuthInputControls(
                                tokenInput = uiState.tokenInput,
                                onTokenInputChanged = { onEvent(AuthEvent.TokenInputChanged(it)) },
                                isOfflineModeEnabled = uiState.isOfflineModeEnabled,
                                onOfflineModeToggled = { onEvent(AuthEvent.OfflineModeToggled(it)) },
                                onSubmitClick = { onEvent(AuthEvent.LoginWithBrowserClicked) },
                                onGuestClick = { onEvent(AuthEvent.GuestModeClicked) }
                            )
                        }
                    }
                }

                // Bottom Hardware Telemetry Strip & Frame
                item {
                    AuthBottomTelemetryStrip()
                }
            }
        }
    }
}

// === COMPOSE PREVIEWS ===

@Preview(showBackground = true, name = "Auth Screen - Form State")
@Composable
fun AuthScreenFormPreview() {
    GHClientTheme {
        AuthScreen(
            uiState = AuthState(
                tokenInput = "ghp_9A8174b09C883dE11",
                isOfflineModeEnabled = true
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Auth Screen - Loading State")
@Composable
fun AuthScreenLoadingPreview() {
    GHClientTheme {
        AuthScreen(
            uiState = AuthState(
                isLoading = true
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@Preview(showBackground = true, name = "Auth Screen - Error State")
@Composable
fun AuthScreenErrorPreview() {
    GHClientTheme {
        AuthScreen(
            uiState = AuthState(
                errorMessage = "Неверный маркер доступа ГОСТ"
            ),
            onEvent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
