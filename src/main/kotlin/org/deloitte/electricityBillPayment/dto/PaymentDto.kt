package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class PaymentDto(
    @field:NotNull(message = "Bill ID cannot be null")
    val billId: Long,
    
    @field:NotNull(message = "Payment method ID cannot be null")
    val paymentMethodId: Long,
    
    @field:NotNull(message = "Amount cannot be null")
    @field:Min(value = 1, message = "Amount must be greater than 0")
    val amount: Double
)
