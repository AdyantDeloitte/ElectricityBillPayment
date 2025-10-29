package org.deloitte.electricityBillPayment.dto

import java.time.LocalDateTime

data class PaymentResponse(
    val transactionId: Long,
    val billId: Long,
    val billNumber: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String,
    val transactionDate: LocalDateTime,
    val message: String = "Payment processed successfully"
)
