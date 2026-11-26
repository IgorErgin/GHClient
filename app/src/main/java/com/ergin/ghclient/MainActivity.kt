package com.ergin.ghclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ergin.ghclient.core.ui.components.CathodeBottomNavigationBar
import com.ergin.ghclient.core.ui.components.CathodeBottomTab
import com.ergin.ghclient.navigation.FavoritesDestination
import com.ergin.ghclient.navigation.GHClientNavHost
import com.ergin.ghclient.navigation.ProfileDestination
import com.ergin.ghclient.navigation.RepoDetailsDestination
import com.ergin.ghclient.navigation.SearchDestination
import com.ergin.ghclient.ui.theme.GHClientTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GHClientTheme {
                val controller = rememberNavController()
                navController = controller

                val navBackStackEntry by controller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isAuthScreen = currentRoute?.contains("AuthDestination") == true

                val selectedTab = when {
                    currentRoute?.contains("SearchDestination") == true -> CathodeBottomTab.REPOS
                    currentRoute?.contains("FavoritesDestination") == true -> CathodeBottomTab.ROM
                    currentRoute?.contains("RepoDetailsDestination") == true -> CathodeBottomTab.DETAILS
                    currentRoute?.contains("ProfileDestination") == true -> CathodeBottomTab.OPERATOR
                    else -> CathodeBottomTab.REPOS
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isAuthScreen) {
                            CathodeBottomNavigationBar(
                                selectedTab = selectedTab,
                                onTabSelected = { tab ->
                                    when (tab) {
                                        CathodeBottomTab.REPOS -> {
                                            controller.navigate(SearchDestination) {
                                                popUpTo(SearchDestination) { inclusive = true }
                                            }
                                        }
                                        CathodeBottomTab.ROM -> {
                                            controller.navigate(FavoritesDestination)
                                        }
                                        CathodeBottomTab.DETAILS -> {
                                            controller.navigate(RepoDetailsDestination(owner = "jetbrains", repoName = "kotlin"))
                                        }
                                        CathodeBottomTab.OPERATOR -> {
                                            controller.navigate(ProfileDestination)
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    GHClientNavHost(
                        navController = controller,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController?.handleDeepLink(intent)
    }
}
