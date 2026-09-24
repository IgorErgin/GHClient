package com.ergin.ghclient.core.domain

/**
 * Иерархия ошибок для бизнес-логики.
 */
sealed interface DomainError {
    
    enum class Network : DomainError {
        TIMEOUT,
        NO_INTERNET,
        SERVER_ERROR,
        UNAUTHORIZED, // Для перехвата 401 и логаута
        NOT_FOUND,
        UNKNOWN
    }
    
    enum class Local : DomainError {
        IO_EXCEPTION,
        UNKNOWN
    }
}
