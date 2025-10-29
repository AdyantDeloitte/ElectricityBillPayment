package org.deloitte.electricityBillPayment.infrastructure.exception


import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant
import org.slf4j.LoggerFactory
import kotlin.Exception

@ControllerAdvice
class RestExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BillException::class)
    fun handleBillServiceException(ex: BillException): ResponseEntity<ApiError> {
        log.error("Service error", ex)
        val error = ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = ex.message ?: "Service error", timestamp = Instant.now())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiError> {
        log.error("Unhandled exception", ex)
        val error = ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Unexpected error", timestamp = Instant.now())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<ApiError> {
        log.error("BadRequest exception", ex)
        val error = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = ex.message, timestamp = Instant.now())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequestException(ex: IllegalArgumentException): ResponseEntity<ApiError> {
        log.error("IllegalArgumentException exception", ex)
        val error = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = ex.message, timestamp = Instant.now())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(UserException::class)
    fun handleUserException(ex: UserException): ResponseEntity<ApiError> {
        log.error("User Service exception", ex)
        val error = ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = ex.message, timestamp = Instant.now())
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}