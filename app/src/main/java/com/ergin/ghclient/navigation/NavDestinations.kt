package com.ergin.ghclient.navigation

import kotlinx.serialization.Serializable

/**
 * Типизированные дестинейшены навигации Compose Navigation
 */
@Serializable
data object AuthDestination

@Serializable
data object SearchDestination

@Serializable
data class RepoDetailsDestination(
    val owner: String,
    val repoName: String
)

@Serializable
data object FavoritesDestination

@Serializable
data object ProfileDestination
