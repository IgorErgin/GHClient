package com.ergin.ghclient.core.network

import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): Result<T, DomainError.Network> {
    return try {
        Result.Success(apiCall())
    } catch (e: HttpException) {
        when (e.code()) {
            401 -> Result.Error(DomainError.Network.UNAUTHORIZED)
            404 -> Result.Error(DomainError.Network.NOT_FOUND)
            in 500..599 -> Result.Error(DomainError.Network.SERVER_ERROR)
            else -> Result.Error(DomainError.Network.UNKNOWN)
        }
    } catch (e: IOException) {
        Result.Error(DomainError.Network.NO_INTERNET)
    } catch (e: Exception) {
        Result.Error(DomainError.Network.UNKNOWN)
    }
}
