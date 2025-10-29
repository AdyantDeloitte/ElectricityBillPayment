package org.deloitte.electricityBillPayment.dto

data class UserRegisterRequest(
    var username: String,
    var name: String,
    var email: String,
    var mobile: String,
    var password: String,
    var hintId: String,
    var hintAnswer: String
)