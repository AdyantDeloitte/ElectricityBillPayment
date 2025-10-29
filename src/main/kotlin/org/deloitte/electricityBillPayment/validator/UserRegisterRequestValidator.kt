package org.deloitte.electricityBillPayment.validator

import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.deloitte.electricityBillPayment.util.isValidEmail
import org.deloitte.electricityBillPayment.util.isValidMobile
import org.deloitte.electricityBillPayment.util.isValidUsername
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.stereotype.Component

@Component
class UserRegisterRequestValidator(
    private val userRepository: UserRepository
) {

    private val log = logger<UserRegisterRequestValidator>()

    fun validateRegisterRequest(registerRequest: UserRegisterRequest) {
        with(registerRequest) {
            require(username.isNotBlank()) { "Username cannot be blank" }
            require(name.isNotBlank()) { "Name cannot be blank" }
            require(email.isNotBlank()) { "Email cannot be blank" }
            require(mobile.isNotBlank()) { "Mobile cannot be blank" }
            require(password.isNotBlank()) { "Password cannot be blank" }
            require(hintId.isNotBlank()) { "Hint ID cannot be blank" }
            require(hintAnswer.isNotBlank()) { "Hint answer cannot be blank" }
            
            require(username.isValidUsername()) { "Username format is invalid" }
            require(email.isValidEmail()) { "Email format is invalid" }
            require(mobile.isValidMobile()) { "Mobile format is invalid" }

            if (userRepository.existsByEmail(email)) {
                log.warn("Registration attempt with existing email: {}", email)
                throw IllegalArgumentException("Email already exists!")
            }
            if (userRepository.existsByUsername(username)) {
                log.warn("Registration attempt with existing username: {}", username)
                throw IllegalArgumentException("Username already exists!")
            }
            if (userRepository.existsByMobile(mobile)) {
                log.warn("Registration attempt with existing mobile: {}", mobile)
                throw IllegalArgumentException("Mobile already exists!")
            }
        }
    }
}