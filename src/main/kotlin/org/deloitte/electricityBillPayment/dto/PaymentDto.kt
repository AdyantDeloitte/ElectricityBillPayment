package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Payment Request DTO")
data class PaymentDto(
    @field:NotNull(message = "Bill ID cannot be null")
    @field:Min(value = 1, message = "Bill ID must be greater than 0")
    val billId: Long,
    
    @field:NotNull(message = "Payment method ID cannot be null")
    @field:Min(value = 1, message = "Payment method ID must be greater than 0")
    val paymentMethodId: Long,
    
    @field:NotNull(message = "Amount cannot be null")
    @field:Min(value = 1, message = "Amount must be greater than 0")
    val amount: Double
)
