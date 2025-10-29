package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.service.UserService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    private val log = logger<UserController>()

    @PostMapping("/register")
    fun userSignup(@Valid @RequestBody userRegisterRequest: UserRegisterRequest): ResponseEntity<ApiResponse<UserRegisterResponse>> {
        log.info("Starting user registration")
        return try {
            val response = userService.userSignUp(userRegisterRequest)
            ResponseEntity.ok(response.toSuccessResponse("User registered successfully"))
        } catch (ex: Exception) {
            log.error("Error during user registration", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to register user: ${ex.message}",
                    code = 500
                )
            )
        }
    }
}