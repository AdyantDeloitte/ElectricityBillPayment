package org.deloitte.electricityBillPayment.dto

import java.time.Instant

data class StandardErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

