package org.deloitte.electricityBillPayment.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Bill creation request")
data class BillCreateRequest(
    @field:NotNull val userId: Long?,
    @field:NotBlank val consumerName: String,
    @field:NotBlank val uniqueServiceNumber: String,
    @field:NotBlank val serviceNumber: String,
    @field:NotBlank val eroName: String,
    @field:NotBlank val address: String,
    @field:NotNull val currentMonthBillDate: LocalDate?,
    @field:NotNull @field:DecimalMin("0.0") val currentMonthBillDateAmount: Double?,
    @field:NotNull val arrearsDate: LocalDate?,
    @field:NotNull @field:DecimalMin("0.0") val arrearsDateAmount: Double?,
    @field:NotNull val totalAmountDueDate: LocalDate?,
    @field:NotNull @field:DecimalMin("0.0") val totalAmountDueDateAmount: Double?,
    @field:NotNull val lastMonthPaidDate: LocalDate?,
    @field:NotNull @field:DecimalMin("0.0") val amountPaidCurrentMonth: Double?
)

