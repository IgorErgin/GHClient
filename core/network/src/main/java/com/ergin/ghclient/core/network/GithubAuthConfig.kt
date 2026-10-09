package com.ergin.ghclient.core.network

/**
 * Конфигурация GitHub OAuth 2.0 авторизации.
 */
object GithubAuthConfig {
    const val REDIRECT_URI = "ghclient://oauth/callback"
    const val AUTH_ENDPOINT = "https://github.com/login/oauth/authorize"
    const val TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token"
    const val SCOPES = "user,repo,read:org,gist"
}
