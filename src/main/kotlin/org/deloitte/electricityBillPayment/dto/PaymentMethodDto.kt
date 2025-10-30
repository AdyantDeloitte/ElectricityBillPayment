package org.deloitte.electricityBillPayment.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Payment Method DTO")
data class PaymentMethodDto(
    val id: Long?,
    val methodName: String,
    val status: String
)
