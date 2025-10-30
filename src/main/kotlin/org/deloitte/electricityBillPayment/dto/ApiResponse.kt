package org.deloitte.electricityBillPayment.dto

import java.time.Instant
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Field-level validation error")
data class ValidationError(
    @Schema(description = "Name of the invalid field", example = "email")
    val field: String,
    @Schema(description = "Validation error message", example = "must be a well-formed email address")
    val message: String
)

@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(description = "True for success, false for failure", example = "true")
    val status: Boolean,
    @Schema(description = "Human-readable message", example = "Operation completed successfully")
    val message: String,
    @Schema(description = "Response payload; null on errors")
    val data: T? = null,
    @Schema(description = "List of field-level validation errors; present only for 400 cases")
    val errors: List<ValidationError>? = null,
    @Schema(description = "Response timestamp in UTC", example = "2025-01-01T10:00:00Z")
    val timestamp: Instant = Instant.now()
)

fun <T> T.toSuccessResponse(message: String = "Success"): ApiResponse<T> =
    ApiResponse(status = true, message = message, data = this)

fun <T> errorResponse(message: String, data: T? = null, errors: List<ValidationError>? = null): ApiResponse<T> =
    ApiResponse(status = false, message = message, data = data, errors = errors)
