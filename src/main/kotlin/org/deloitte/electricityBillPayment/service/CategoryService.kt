package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.CategoryDto
import org.deloitte.electricityBillPayment.dto.SubCategoryDto
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.repository.CategoryRepository
import org.deloitte.electricityBillPayment.repository.SubCategoryRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository
) {
    private val log = logger<CategoryService>()

    @Transactional(readOnly = true)
    fun getAllCategories(): List<CategoryDto> {
        log.debug("Fetching all categories with subcategories")
        return try {
            val categories = categoryRepository.findAll()
            val dtos = categories.map { it.toDto() }
            log.info("Retrieved {} categories", dtos.size)
            dtos
        } catch (ex: Exception) {
            log.error("Error while fetching categories", ex)
            throw ex
        }
    }

    @Transactional(readOnly = true)
    fun getAllSubCategories(): List<SubCategoryDto> {
        log.debug("Fetching all subcategories")
        return try {
            val subs = subCategoryRepository.findAll()
            val dtos = subs.map { it.toDto() }
            log.info("Retrieved {} subcategories", dtos.size)
            dtos
        } catch (ex: Exception) {
            log.error("Error while fetching subcategories", ex)
            throw ex
        }
    }

    @Transactional(readOnly = true)
    fun getSubCategoriesByCategoryId(categoryId: Long): List<SubCategoryDto> {
        log.debug("Fetching subcategories for category id: {}", categoryId)
        return try {
            val subs = subCategoryRepository.findByCategoryId(categoryId)
            val dtos = subs.map { it.toDto() }
            log.info("Retrieved {} subcategories for category {}", dtos.size, categoryId)
            dtos
        } catch (ex: Exception) {
            log.error("Error while fetching subcategories for category id: {}", categoryId, ex)
            throw ex
        }
    }
}
