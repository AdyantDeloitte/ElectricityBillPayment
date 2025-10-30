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