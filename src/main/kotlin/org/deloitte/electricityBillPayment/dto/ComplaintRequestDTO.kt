package org.deloitte.electricityBillPayment.dto

data class ComplaintRequestDTO(
    val serviceNumber: String,
    val subCategoryId: Long?,
    val categoryId: Long?,
    val name: String,
    val email: String,
    val mobile: String,
    val userId: Long?,
    val documentPath: String
)