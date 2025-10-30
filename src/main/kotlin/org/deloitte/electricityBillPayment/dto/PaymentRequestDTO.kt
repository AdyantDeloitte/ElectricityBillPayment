package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.*

data class PaymentRequestDTO(
    @field:NotNull(message = "userId is required")
    val userId: Long?,

    @field:NotNull(message = "billId is required")
    val billId: Long?,

    @field:NotNull(message = "paymentMethodId is required")
    val paymentMethodId: Long?,

    @field:NotNull(message = "amount is required")
    @field:DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    val amount: Double?,

    @field:NotBlank(message = "nameOnCard cannot be blank")
    @field:Size(min = 2, max = 100)
    val nameOnCard : String,

    @field:Min(0)
    @field:Max(9999)
    val cardLast4 :Int?,

    @field:Min(1)
    @field:Max(12)
    val expiryMonth :Int?,

    @field:Min(2024)
    @field:Max(2100)
    val expiryYear: Int?,

    @field:Min(100)
    @field:Max(999)
    val cVV: Int?
)

