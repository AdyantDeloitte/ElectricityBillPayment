package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.UserLoginRequest
import org.deloitte.electricityBillPayment.dto.UserLoginResponse
import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.entity.User
import org.deloitte.electricityBillPayment.exception.UserException
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.repository.HintRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.deloitte.electricityBillPayment.validator.UserRegisterRequestValidator
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.IllegalArgumentException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val hintRepository: HintRepository,
    private val billService: BillService,
    private val userRegisterRequestValidator: UserRegisterRequestValidator,
    private val passwordEncoder: PasswordEncoder) {

    private val log = logger<UserService>()

    fun userSignUp(userRegisterRequest: UserRegisterRequest): UserRegisterResponse {
        log.info("Validating user registration request")
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

        return try {
            val savedUser = userRepository.save(userEntity)
            log.info("User registered successfully with user_id: ${savedUser.id}")
            savedUser.toDto()
        } catch (e: Exception) {
            log.error("Error occurred while registering user", e)
            throw UserException("Error occurred while registering user: ${e.message}")
        }
    }

    fun userLogin(userLoginRequest: UserLoginRequest): UserLoginResponse{
        val loginInput = userLoginRequest.usernameOrEmail
        val password = userLoginRequest.password

        val user = userRepository.findByEmail(loginInput) ?:
                    userRepository.findByUsername(loginInput)
                        ?: throw UserException("Invalid username or email")

        if(!passwordEncoder.matches(password, user.password)){
            log.warn("Invalid password attempt for user: ${user.username}")
            throw UserException("Invalid Password")
        }
        log.info("user: ${user.username} logged in successfully")
        val bills = billService.getBillsByUserId(user.id!!)
        log.info("total bills fetched: ${bills.size}")

        return UserLoginResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            mobile = user.mobile,
            bills = bills.map{ it.toDto() }
        )
    }
}