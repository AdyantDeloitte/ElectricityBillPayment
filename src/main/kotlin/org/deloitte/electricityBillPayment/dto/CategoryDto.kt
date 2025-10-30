package org.deloitte.electricityBillPayment.dto

data class CategoryDto(
    val id: Long?,
    val name: String,
    val subcategories: List<SubCategoryDto> = emptyList()
)

data class SubCategoryDto(
    val id: Long?,
    val name: String
)

