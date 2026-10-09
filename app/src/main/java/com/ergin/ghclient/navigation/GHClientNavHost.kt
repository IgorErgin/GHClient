package com.ergin.ghclient.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.ergin.ghclient.feature.auth.impl.presentation.ui.AuthRoute
import com.ergin.ghclient.feature.favorites.impl.presentation.ui.FavoritesRoute
import com.ergin.ghclient.feature.profile.impl.presentation.ui.ProfileRoute
import com.ergin.ghclient.feature.repository.impl.presentation.ui.RepoDetailsRoute
import com.ergin.ghclient.feature.search.impl.presentation.ui.SearchRoute

@Composable
fun GHClientNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = AuthDestination
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<AuthDestination>(
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "ghclient://oauth/callback?code={code}"
                }
            )
        ) { backStackEntry ->
            val code = backStackEntry.arguments?.getString("code")
            AuthRoute(
                onNavigateToHome = {
                    navController.navigate(SearchDestination) {
                        popUpTo<AuthDestination> { inclusive = true }
                    }
                },
                oauthCode = code
            )
        }

        composable<SearchDestination> {
            SearchRoute(
                onNavigateToDetails = { owner, repoName ->
                    navController.navigate(RepoDetailsDestination(owner = owner, repoName = repoName))
                }
            )
        }

        composable<FavoritesDestination> {
            FavoritesRoute(
                onNavigateToDetails = { owner, repoName ->
                    navController.navigate(RepoDetailsDestination(owner = owner, repoName = repoName))
                }
            )
        }

        composable<RepoDetailsDestination> {
            RepoDetailsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCreateIssue = { owner, repoName ->
                    Toast.makeText(
                        context,
                        "Формирование рапорта: $owner/$repoName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable<ProfileDestination> {
            ProfileRoute(
                onNavigateToAuth = {
                    navController.navigate(AuthDestination) {
                        popUpTo<SearchDestination> { inclusive = true }
                    }
                }
            )
        }
    }
}
