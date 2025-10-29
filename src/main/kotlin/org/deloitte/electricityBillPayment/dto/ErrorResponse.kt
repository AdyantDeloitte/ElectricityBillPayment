package org.deloitte.electricityBillPayment.dto

import java.time.Instant

data class ErrorResponse(
    val timestamp: Instant = Instant.now(),
    val message: String,
    val details: String? = null,
    val code: Int,
    val path: String? = null,
    val errors: List<FieldError>? = null
)

data class FieldError(
    val field: String,
    val message: String,
    val rejectedValue: Any?
)

object ErrorCodes {
    const val VALIDATION_ERROR = 400
    const val UNAUTHORIZED = 401
    const val FORBIDDEN = 403
    const val NOT_FOUND = 404
    const val CONFLICT = 409
    const val INTERNAL_SERVER_ERROR = 500
    const val SERVICE_UNAVAILABLE = 503
}
