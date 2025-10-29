package org.deloitte.electricityBillPayment.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class BillDto(
    val id: Long?,
    val consumerName: String,
    val uniqueServiceNumber: String,
    val serviceNumber: String,
    val eroName: String,
    val address: String,
    val currentMonthBillDate: LocalDate,
    val currentMonthBillDateAmount: Double,
    val arrearsDate: LocalDate,
    val arrearsDateAmount: Double,
    val totalAmountDueDate: LocalDate,
    val totalAmountDueDateAmount: Double,
    val lastMonthPaidDate: LocalDate,
    val amountPaidCurrentMonth: Double,
    val status: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
