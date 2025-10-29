package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.entity.User
import org.deloitte.electricityBillPayment.infrastructure.exception.UserException
import org.deloitte.electricityBillPayment.repository.HintRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.deloitte.electricityBillPayment.validator.UserRegisterRequestValidator
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.IllegalArgumentException

@Service
class UserService(
    private var userRepository: UserRepository,
    private var hintRepository: HintRepository,
    private var userRegisterRequestValidator: UserRegisterRequestValidator,
    private var passwordEncoder: PasswordEncoder) {

    private var logger = LoggerFactory.getLogger(UserService::class.java)

    fun userSignUp(userRegisterRequest: UserRegisterRequest): UserRegisterResponse{
        logger.info("validating user registration request")
        userRegisterRequestValidator.validateRegisterRequest(userRegisterRequest)

        val hint = hintRepository.findById(userRegisterRequest.hintId.toLong())
            .orElseThrow { IllegalArgumentException("Invalid hint_id: ${userRegisterRequest.hintId}") }

        val userEntity = User().apply {
            username = userRegisterRequest.username
            name = userRegisterRequest.name
            email = userRegisterRequest.email
            mobile = userRegisterRequest.mobile
            password = passwordEncoder.encode(userRegisterRequest.password)
            this.hint = hint
            hintAnswer = userRegisterRequest.hintAnswer

            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }

        try{
            val savedUser = userRepository.save<User>(userEntity)
            logger.info("user registered successfully with user_id: ${savedUser.id}")

            return UserRegisterResponse(
                userId = savedUser.id,
                userName = savedUser.username,
                name = savedUser.name,
                email = savedUser.email,
                mobile = savedUser.mobile
            )

        } catch(e: Exception){
            logger.error("error occurred while registering user!")
            throw UserException("error occurred while registering user:")
        }
    }
}