package org.deloitte.electricityBillPayment.dto

import java.time.LocalDateTime
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Payment Response DTO")
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
