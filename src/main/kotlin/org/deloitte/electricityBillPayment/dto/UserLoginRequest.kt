package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.NotBlank

data class UserLoginRequest(

    @field:NotBlank(message = "Username or email cannot be blank")
    var usernameOrEmail: String,

    @field:NotBlank(message = "password cannot be blank")
    var password: String
)