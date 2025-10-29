package org.deloitte.electricityBillPayment.dto

data class UserRegisterResponse(
    var userId: Long?,
    var userName: String,
    var name: String,
    var email: String,
    var mobile: String
)