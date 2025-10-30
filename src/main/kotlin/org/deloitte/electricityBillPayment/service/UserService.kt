package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.UserLoginRequest
import org.deloitte.electricityBillPayment.dto.UserLoginResponse
import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.entity.User
import org.deloitte.electricityBillPayment.exception.UserException
import org.deloitte.electricityBillPayment.exception.ResourceNotFoundException
import org.deloitte.electricityBillPayment.exception.ApiException
import org.deloitte.electricityBillPayment.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.repository.HintRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
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
    private val passwordEncoder: PasswordEncoder) {

    private val log = logger<UserService>()

    fun userSignUp(userRegisterRequest: UserRegisterRequest): UserRegisterResponse {
        log.info("Validating user registration request")

        val hint = hintRepository.findById(userRegisterRequest.hintId.toLong())
            .orElseThrow { ApiException(ErrorCode.NOT_FOUND, "Invalid hint_id: ${userRegisterRequest.hintId}", HttpStatus.NOT_FOUND) }

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

        if (userRepository.existsByUsername(userRegisterRequest.username)) {
            throw ApiException(ErrorCode.CONFLICT, "Username already exists", HttpStatus.CONFLICT)
        }
        if (userRepository.existsByEmail(userRegisterRequest.email)) {
            throw ApiException(ErrorCode.CONFLICT, "Email already exists", HttpStatus.CONFLICT)
        }
        if (userRepository.existsByMobile(userRegisterRequest.mobile)) {
            throw ApiException(ErrorCode.CONFLICT, "Mobile already exists", HttpStatus.CONFLICT)
        }

        val savedUser = userRepository.save(userEntity)
        log.info("User registered successfully with user_id: ${savedUser.id}")
        return savedUser.toDto()
    }

    fun userLogin(userLoginRequest: UserLoginRequest): UserLoginResponse{
        val loginInput = userLoginRequest.usernameOrEmail
        val password = userLoginRequest.password

        val user = userRepository.findByEmail(loginInput) ?:
                    userRepository.findByUsername(loginInput)
                        ?: throw ApiException(ErrorCode.NOT_FOUND, "Invalid username or email", HttpStatus.NOT_FOUND)

        if(!passwordEncoder.matches(password, user.password)){
            log.warn("Invalid password attempt for user: ${user.username}")
            throw ApiException(ErrorCode.UNAUTHORIZED, "Invalid password", HttpStatus.UNAUTHORIZED)
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