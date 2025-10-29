package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRegisterRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    val name: String,
    
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Invalid email format")
    val email: String,
    
    @field:NotBlank(message = "Mobile cannot be blank")
    @field:Pattern(regexp = "^[0-9]{10}$", message = "Mobile must be exactly 10 digits")
    val mobile: String,
    
    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,
    
    @field:NotBlank(message = "Hint ID cannot be blank")
    val hintId: String,
    
    @field:NotBlank(message = "Hint answer cannot be blank")
    @field:Size(min = 2, max = 100, message = "Hint answer must be between 2 and 100 characters")
    val hintAnswer: String
)