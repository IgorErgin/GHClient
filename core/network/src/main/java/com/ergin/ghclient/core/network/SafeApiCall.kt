package com.ergin.ghclient.core.network

import android.util.Log
import com.ergin.ghclient.core.domain.DomainError
import com.ergin.ghclient.core.domain.Result
import retrofit2.HttpException
import java.io.IOException

const val NETWORK_LOG_TAG = "NetworkLayer"

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): Result<T, DomainError.Network> {
    return try {
        val data = apiCall()
        Log.d(NETWORK_LOG_TAG, "HTTP Request Succeeded: ${data?.javaClass?.simpleName ?: "Unit"}")
        Result.Success(data)
    } catch (e: HttpException) {
        Log.e(NETWORK_LOG_TAG, "HTTP Exception ${e.code()}: ${e.message()}", e)
        when (e.code()) {
            401 -> Result.Error(DomainError.Network.UNAUTHORIZED)
            404 -> Result.Error(DomainError.Network.NOT_FOUND)
            in 500..599 -> Result.Error(DomainError.Network.SERVER_ERROR)
            else -> Result.Error(DomainError.Network.UNKNOWN)
        }
    } catch (e: IOException) {
        Log.e(NETWORK_LOG_TAG, "Network IO Exception (No Internet / Timeout): ${e.message}", e)
        Result.Error(DomainError.Network.NO_INTERNET)
    } catch (e: Exception) {
        Log.e(NETWORK_LOG_TAG, "Unexpected Network Exception: ${e.message}", e)
        Result.Error(DomainError.Network.UNKNOWN)
    }
}
