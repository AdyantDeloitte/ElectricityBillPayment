package org.deloitte.electricityBillPayment.dto

data class PaymentResponseDTO(
    val transactionId: Long,
    val paymentStatus: String
)