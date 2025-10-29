package org.deloitte.electricityBillPayment.dto

data class ComplaintResponseDTO(
    val id: Long,
    val serviceNumber: String,
    val subCategory: String?,
    val category: String?,
    val name: String,
    val email: String,
    val mobile: String,
    val status: String?,
    val createdAt: String,
)