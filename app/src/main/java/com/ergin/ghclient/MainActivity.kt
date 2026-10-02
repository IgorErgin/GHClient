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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ergin.ghclient.feature.repository.impl.presentation.ui.RepoDetailsRoute
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
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "search",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("search") {
                            SearchRoute(
                                onNavigateToDetails = { owner, repoName ->
                                    navController.navigate("details/$owner/$repoName")
                                }
                            )
                        }

                        composable(
                            route = "details/{owner}/{repoName}",
                            arguments = listOf(
                                navArgument("owner") { type = NavType.StringType },
                                navArgument("repoName") { type = NavType.StringType }
                            )
                        ) {
                            RepoDetailsRoute(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToCreateIssue = { owner, repoName ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Формирование рапорта: $owner/$repoName",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
