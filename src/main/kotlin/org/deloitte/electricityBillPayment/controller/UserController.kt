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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as OasApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/users")
@Tag(name = "User", description = "User API operations")
class UserController(private val userService: UserService) {

    private val log = logger<UserController>()

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user")
    @ApiResponses(
        value = [
            OasApiResponse(responseCode = "200", description = "User registered successfully", content = [Content(schema = Schema(implementation = org.deloitte.electricityBillPayment.dto.ApiResponse::class))]),
            OasApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun userSignup(@Valid @RequestBody userRegisterRequest: UserRegisterRequest): ResponseEntity<ApiResponse<UserRegisterResponse>> {
        log.info("Starting user registration")
        val response = userService.userSignUp(userRegisterRequest)
        return ResponseEntity.ok(response.toSuccessResponse("User registered successfully"))
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns profile & bills")
    @ApiResponses(
        value = [
            OasApiResponse(responseCode = "200", description = "Login successful", content = [Content(schema = Schema(implementation = org.deloitte.electricityBillPayment.dto.ApiResponse::class))]),
            OasApiResponse(responseCode = "400", description = "Invalid input", content = [Content()])
        ]
    )
    fun userSignIn(@Valid @RequestBody userLoginRequest: UserLoginRequest): ResponseEntity<ApiResponse<UserLoginResponse>>{
        val response = userService.userLogin(userLoginRequest)
        return ResponseEntity.ok(response.toSuccessResponse("user logged in successfully"))
    }
}