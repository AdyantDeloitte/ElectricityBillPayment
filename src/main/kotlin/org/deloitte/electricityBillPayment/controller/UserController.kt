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
import org.deloitte.electricityBillPayment.dto.UserLoginRequest
import org.deloitte.electricityBillPayment.dto.UserLoginResponse

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

    @PostMapping("/login")
    fun userSignIn(@RequestBody userLoginRequest: UserLoginRequest): ResponseEntity<ApiResponse<UserLoginResponse>>{
        return try{
            val response = userService.userLogin(userLoginRequest)
            ResponseEntity.ok(response.toSuccessResponse("user logged in successfully"))
        } catch (ex: Exception){
            log.error("Error during user login", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to login user: ${ex.message}",
                    code = 500
                )
            )
        }
    }
}