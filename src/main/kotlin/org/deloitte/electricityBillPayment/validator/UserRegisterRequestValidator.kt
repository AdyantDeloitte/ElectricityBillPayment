package org.deloitte.electricityBillPayment.validator

import org.deloitte.electricityBillPayment.dto.UserRegisterRequest
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserRegisterRequestValidator(
    private val userRepository: UserRepository
) {

    fun validateRegisterRequest(registerRequest: UserRegisterRequest){
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        with(registerRequest){
            require(username.isNotBlank()){"username cannot be Null"}
            require(name.isNotBlank()){"name cannot be Null"}
            require(email.isNotBlank()){"email cannot be Null"}
            require(mobile.isNotBlank()){"mobile cannot be Null"}
            require(password.isNotBlank()){"password cannot be Null"}
            require(hintId.isNotBlank()){"Hint_Id cannot be Null"}
            require(hintAnswer.isNotBlank()){"hintAnswer cannot be Null"}
            require(email.matches(emailRegex)){"email is invalid"}

            if(userRepository.existsByEmail(email)){
                throw IllegalArgumentException("Email already exist!")
            }
        }
    }
}