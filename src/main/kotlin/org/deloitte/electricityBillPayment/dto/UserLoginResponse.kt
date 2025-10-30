package org.deloitte.electricityBillPayment.dto

data class UserLoginResponse(
    val id: Long?,
    val username: String,
    val name: String,
    val email: String,
    val mobile: String,
    val bills: List<BillDto>
)