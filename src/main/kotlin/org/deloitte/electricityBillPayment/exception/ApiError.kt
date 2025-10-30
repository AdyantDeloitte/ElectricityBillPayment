package org.deloitte.electricityBillPayment.exception

import java.time.Instant

data class ApiError(
    val status: Int,
    val message: String?,
    val timestamp: Instant
)