package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.NotBlank
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "User login request")
data class UserLoginRequest(

    @field:NotBlank(message = "Username or email cannot be blank")
    @Schema(description = "Username or email identifier", example = "adyant.singh")
    val usernameOrEmail: String,

    @field:NotBlank(message = "password cannot be blank")
    @Schema(description = "User password", example = "Bh0pal@2025")
    val password: String
)