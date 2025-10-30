package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.CategoryDto
import org.deloitte.electricityBillPayment.dto.SubCategoryDto
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.service.CategoryService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(private val categoryService: CategoryService) {

    private val log = logger<CategoryController>()

    @GetMapping
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryDto>>> {
        log.info("Received request to get all categories")
        return try {
            val categories = categoryService.getAllCategories()
            ResponseEntity.ok(categories.toSuccessResponse("Categories retrieved successfully"))
        } catch (ex: Exception) {
            log.error("Error while retrieving categories", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to retrieve categories: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @GetMapping("/subcategories")
    fun getAllSubCategories(): ResponseEntity<ApiResponse<List<SubCategoryDto>>> {
        log.info("Received request to get all subcategories")
        return try {
            val subs = categoryService.getAllSubCategories()
            ResponseEntity.ok(subs.toSuccessResponse("Subcategories retrieved successfully"))
        } catch (ex: Exception) {
            log.error("Error while retrieving subcategories", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to retrieve subcategories: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    // New endpoint: get subcategories by parent category id
    @GetMapping("/{categoryId}/subcategories")
    fun getSubCategoriesByCategoryId(@PathVariable categoryId: Long): ResponseEntity<ApiResponse<List<SubCategoryDto>>> {
        log.info("Received request to get subcategories for category id: {}", categoryId)
        return try {
            val subs = categoryService.getSubCategoriesByCategoryId(categoryId)
            ResponseEntity.ok(subs.toSuccessResponse("Subcategories retrieved successfully for category $categoryId"))
        } catch (ex: Exception) {
            log.error("Error while retrieving subcategories for category id: {}", categoryId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to retrieve subcategories: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }
}
