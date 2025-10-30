package org.deloitte.electricityBillPayment.exception

import org.springframework.http.HttpStatus

enum class ErrorCode {
    VALIDATION_ERROR,
    NOT_FOUND,
    CONFLICT,
    UNAUTHORIZED,
    FORBIDDEN,
    INTERNAL_ERROR
}

class ApiException(
    val errorCode: ErrorCode,
    override val message: String,
    val status: HttpStatus
) : RuntimeException(message)

