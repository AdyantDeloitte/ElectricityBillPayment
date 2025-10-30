package org.deloitte.electricityBillPayment.exception

import org.apache.coyote.BadRequestException
import org.deloitte.electricityBillPayment.dto.ErrorResponse
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.slf4j.LoggerFactory
import java.time.Instant

@ControllerAdvice
class RestExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(BillException::class)
    fun handleBillServiceException(ex: BillException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.error("Service error", ex)
        val error = ErrorResponse(
            message = ex.message ?: "Service error",
            code = ErrorCodes.INTERNAL_SERVER_ERROR,
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(UserException::class)
    fun handleUserException(ex: UserException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.error("User service error", ex)
        val error = ErrorResponse(
            message = ex.message ?: "User service error",
            code = ErrorCodes.INTERNAL_SERVER_ERROR,
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.warn("Validation error", ex)
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            org.deloitte.electricityBillPayment.dto.FieldError(
                field = fieldError.field,
                message = fieldError.defaultMessage ?: "Invalid value",
                rejectedValue = fieldError.rejectedValue
            )
        }
        val error = ErrorResponse(
            message = "Validation failed",
            code = ErrorCodes.VALIDATION_ERROR,
            path = request.getDescription(false),
            errors = fieldErrors
        )
        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.warn("Illegal argument", ex)
        val error = ErrorResponse(
            message = ex.message ?: "Invalid argument",
            code = ErrorCodes.VALIDATION_ERROR,
            path = request.getDescription(false)
        )
        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.warn("Bad Request", ex)
        val error = ErrorResponse(
            message = ex.message ?: "Bad Request",
            code = ErrorCodes.VALIDATION_ERROR,
            path = request.getDescription(false)
        )
        return ResponseEntity.badRequest().body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", ex)
        val error = ErrorResponse(
            message = "Unexpected error occurred",
            code = ErrorCodes.INTERNAL_SERVER_ERROR,
            path = request.getDescription(false)
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }

    @ExceptionHandler(ComplaintException::class)
    fun handleComplaintException(ex: ComplaintException): ResponseEntity<ApiError>{
        log.error("Complaint service exception", ex)
        val error = ApiError(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = ex.message,
            timestamp = Instant.now()
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}