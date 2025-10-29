package org.deloitte.electricityBillPayment.exception


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
}