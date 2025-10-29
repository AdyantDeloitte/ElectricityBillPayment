package org.deloitte.electricityBillPayment.dto

import java.time.Instant

sealed class ApiResponse<out T> {
    data class Success<T>(
        val data: T,
        val message: String = "Success",
        val timestamp: Instant = Instant.now()
    ) : ApiResponse<T>()
    
    data class Error(
        val message: String,
        val code: Int,
        val details: String? = null,
        val timestamp: Instant = Instant.now()
    ) : ApiResponse<Nothing>()
}

fun <T> T.toSuccessResponse(message: String = "Success"): ApiResponse.Success<T> =
    ApiResponse.Success(data = this, message = message)

fun createErrorResponse(message: String, code: Int, details: String? = null): ApiResponse.Error =
    ApiResponse.Error(message = message, code = code, details = details)
