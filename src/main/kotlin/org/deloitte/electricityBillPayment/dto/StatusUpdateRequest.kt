package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.NotBlank

data class StatusUpdateRequest(

    @field:NotBlank(message = "status cannot be blank")
    val status: String
)