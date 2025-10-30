package org.deloitte.electricityBillPayment.exception

import org.apache.coyote.BadRequestException
import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.errorResponse
import org.deloitte.electricityBillPayment.dto.ValidationError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import jakarta.validation.ConstraintViolationException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class RestExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	private fun pathOf(request: WebRequest): String =
		(request as? ServletWebRequest)?.request?.requestURI ?: request.getDescription(false)

	@ExceptionHandler(ComplaintNotFoundException::class)
	fun handleComplaintNotFound(ex: ComplaintNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("Complaint not found: {}", ex.message)
		val status = HttpStatus.NOT_FOUND
		val body = errorResponse<Nothing>(message = ex.message ?: "Complaint not found")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(UserException::class)
	fun handleUserException(ex: UserException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.error("User service error: {}", ex.message)
		val status = HttpStatus.INTERNAL_SERVER_ERROR
		val body = errorResponse<Nothing>(message = ex.message ?: "User service error")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(ApiException::class)
	fun handleApiException(ex: ApiException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("ApiException: code={}, status={}, message={}", ex.errorCode, ex.status, ex.message)
		val body = errorResponse<Nothing>(message = ex.message)
		return ResponseEntity.status(ex.status).body(body)
	}

	@ExceptionHandler(UserAlreadyExistsException::class)
	fun handleUserAlreadyExists(ex: UserAlreadyExistsException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("User already exists: {}", ex.message)
		val status = HttpStatus.CONFLICT
		val body = errorResponse<Nothing>(message = ex.message ?: "User already exists")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(ResourceNotFoundException::class)
	fun handleResourceNotFound(ex: ResourceNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("Resource not found: {}", ex.message)
		val status = HttpStatus.NOT_FOUND
		val body = errorResponse<Nothing>(message = ex.message ?: "Resource not found")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(UserNotFoundException::class)
	fun handleUserNotFound(ex: UserNotFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("User not found: {}", ex.message)
		val status = HttpStatus.NOT_FOUND
		val body = errorResponse<Nothing>(message = ex.message ?: "User not found")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val errors = ex.bindingResult.fieldErrors.map { fieldError ->
			ValidationError(field = fieldError.field, message = fieldError.defaultMessage ?: "Invalid")
		}
		val message = if (errors.isNotEmpty()) "Validation failed for one or more fields" else "Invalid input"
		log.warn("Validation error(s): {}", errors.joinToString(", ") { "${'$'}{it.field}=${'$'}{it.message}" })
		val status = HttpStatus.BAD_REQUEST
		val body = errorResponse<Nothing>(message = message, errors = errors)
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(ConstraintViolationException::class)
	fun handleConstraintViolation(ex: ConstraintViolationException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val errors = ex.constraintViolations.map { violation ->
			val field = violation.propertyPath?.toString()?.substringAfterLast('.') ?: ""
			ValidationError(field = field, message = violation.message)
		}
		val message = if (errors.isNotEmpty()) "Validation failed for one or more fields" else "Invalid input"
		log.warn("Constraint violation(s): {}", errors.joinToString(", ") { "${'$'}{it.field}=${'$'}{it.message}" })
		val status = HttpStatus.BAD_REQUEST
		val body = errorResponse<Nothing>(message = message, errors = errors)
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val field = ex.name
		val requiredType = ex.requiredType?.simpleName ?: ""
		val message = if (requiredType.isNotEmpty()) "Expected type $requiredType" else "Invalid value"
		val errors = listOf(ValidationError(field = field, message = message))
		val body = errorResponse<Nothing>(message = "Validation failed for one or more fields", errors = errors)
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
	}

	@ExceptionHandler(MissingServletRequestParameterException::class)
	fun handleMissingParam(ex: MissingServletRequestParameterException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val errors = listOf(ValidationError(field = ex.parameterName, message = "Parameter is required"))
		val body = errorResponse<Nothing>(message = "Validation failed for one or more fields", errors = errors)
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
	}

	@ExceptionHandler(HttpMessageNotReadableException::class)
	fun handleUnreadableBody(ex: HttpMessageNotReadableException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val body = errorResponse<Nothing>(message = "Malformed JSON request")
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
	}

	@ExceptionHandler(IllegalArgumentException::class)
	fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("Illegal argument: {}", ex.message)
		val status = HttpStatus.BAD_REQUEST
		val (field, msg) = (ex.message ?: "Invalid argument").split(":", limit = 2).map { it.trim() }.let {
			if (it.size == 2) it[0] to it[1] else "" to (ex.message ?: "Invalid argument")
		}
		val errors = if (field.isNotEmpty()) listOf(ValidationError(field = field, message = msg)) else null
		val body = errorResponse<Nothing>(message = if (errors != null) "Validation failed for one or more fields" else (ex.message
			?: "Invalid argument"), errors = errors)
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(BadRequestException::class)
	fun handleBadRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.warn("Bad Request: {}", ex.message)
		val status = HttpStatus.BAD_REQUEST
		val body = errorResponse<Nothing>(message = ex.message ?: "Bad Request")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(ComplaintException::class)
	fun handleComplaintException(ex: ComplaintException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.error("Complaint service exception: {}", ex.message)
		val status = HttpStatus.INTERNAL_SERVER_ERROR
		val body = errorResponse<Nothing>(message = ex.message ?: "Complaint error")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(NoHandlerFoundException::class)
	fun handleNoHandlerFound(ex: NoHandlerFoundException, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		val status = HttpStatus.NOT_FOUND
		val body = errorResponse<Nothing>(message = "Resource not found")
		return ResponseEntity.status(status).body(body)
	}

	@ExceptionHandler(Exception::class)
	fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ApiResponse<Nothing>> {
		log.error("Unhandled exception: {}", ex.message)
		val status = HttpStatus.INTERNAL_SERVER_ERROR
		val body = errorResponse<Nothing>(message = "Unexpected error occurred")
		return ResponseEntity.status(status).body(body)
	}
}