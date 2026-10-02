package com.ergin.ghclient

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ergin.ghclient.feature.search.impl.presentation.ui.SearchRoute
import com.ergin.ghclient.ui.theme.GHClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GHClientTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchRoute(
                        onNavigateToDetails = { owner, repoName ->
                            Toast.makeText(
                                this,
                                "Выбран репозиторий: $owner/$repoName",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
