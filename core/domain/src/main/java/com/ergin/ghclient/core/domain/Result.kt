package com.ergin.ghclient.core.domain

/**
 * Обертка для всех результатов слоя Domain.
 */
sealed interface Result<out D, out E : DomainError> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E : DomainError>(val error: E) : Result<Nothing, E>
}
