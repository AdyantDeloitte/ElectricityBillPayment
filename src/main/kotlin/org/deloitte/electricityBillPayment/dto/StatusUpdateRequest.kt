package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.NotBlank
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Status update request")
data class StatusUpdateRequest(

    @field:NotBlank(message = "status cannot be blank")
    @Schema(example = "PAID")
    val status: String
)