package de.ljz.questify.core.data.remote.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val code: Int? = null, val message: String? = null) : NetworkResult<Nothing>
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>
}

suspend inline fun <reified T> safeApiCall(call: () -> HttpResponse): NetworkResult<T> {
    return try {
        val response = call()
        if (response.status.value in 200..299) {
            NetworkResult.Success(response.body<T>())
        } else {
            NetworkResult.Error(code = response.status.value, message = response.status.description)
        }
    } catch (e: IOException) {
        NetworkResult.Exception(e)
    } catch (e: SerializationException) {
        NetworkResult.Exception(e)
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}
