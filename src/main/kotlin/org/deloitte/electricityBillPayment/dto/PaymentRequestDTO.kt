package org.deloitte.electricityBillPayment.dto

data class PaymentRequestDTO(
    val userId: Long,
    val billId: Long,
    val paymentMethodId: Long?,
    val amount: Double,
    val nameOnCard : String,
    val cardLast4 :Int,
    val expiryMonth :Int,
    val expiryYear: Int,
    val cVV: Int
)

