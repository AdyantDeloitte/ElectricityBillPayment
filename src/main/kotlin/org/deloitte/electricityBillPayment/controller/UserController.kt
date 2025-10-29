package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private var userService: UserService) {

    private var logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/register")
    fun userSignup(@RequestBody userRegisterRequest: UserRegisterRequest): ResponseEntity<UserRegisterResponse>{
        logger.info("starting user registration")
        val response: UserRegisterResponse = userService.userSignUp(userRegisterRequest)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}