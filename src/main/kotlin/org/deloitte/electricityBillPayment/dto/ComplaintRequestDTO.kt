package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Complaint registration request")
data class ComplaintRequestDTO(
    @field:NotBlank(message = "service number cannot be blank")
    val serviceNumber: String,

    @field:NotNull(message = "subCategoryId is required")
    val subCategoryId: Long?,

    @field:NotNull(message = "categoryId is required")
    val categoryId: Long?,

    @field:NotBlank(message = "name cannot be blank")
    val name: String,

    @field:NotBlank(message = "email cannot be blank")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Mobile cannot be blank")
    @field:Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be exactly 10 digits")
    val mobile: String,

    @field:NotNull(message = "userId is required")
    val userId: Long?,

    @field:NotBlank(message = "document path cannot be blank")
    val documentPath: String
)