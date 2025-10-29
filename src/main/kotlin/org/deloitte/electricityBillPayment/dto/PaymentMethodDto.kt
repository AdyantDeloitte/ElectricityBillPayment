package org.deloitte.electricityBillPayment.dto

data class PaymentMethodDto(
    val id: Long?,
    val methodName: String,
    val status: String
)
