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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as OasApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/categories")
@Tag(name = "Category", description = "Category API operations")
class CategoryController(private val categoryService: CategoryService) {

    private val log = logger<CategoryController>()

    @GetMapping
    @Operation(summary = "List categories", description = "Fetch all categories")
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryDto>>> {
        log.info("Received request to get all categories")
        val categories = categoryService.getAllCategories()
        return ResponseEntity.ok(categories.toSuccessResponse("Categories retrieved successfully"))
    }

    @GetMapping("/subcategories")
    @Operation(summary = "List subcategories", description = "Fetch all subcategories")
    fun getAllSubCategories(): ResponseEntity<ApiResponse<List<SubCategoryDto>>> {
        log.info("Received request to get all subcategories")
        val subs = categoryService.getAllSubCategories()
        return ResponseEntity.ok(subs.toSuccessResponse("Subcategories retrieved successfully"))
    }

    // New endpoint: get subcategories by parent category id
    @GetMapping("/{categoryId}/subcategories")
    @Operation(summary = "List subcategories by category", description = "Fetch subcategories for a category")
    fun getSubCategoriesByCategoryId(@PathVariable categoryId: Long): ResponseEntity<ApiResponse<List<SubCategoryDto>>> {
        log.info("Received request to get subcategories for category id: {}", categoryId)
        val subs = categoryService.getSubCategoriesByCategoryId(categoryId)
        return ResponseEntity.ok(subs.toSuccessResponse("Subcategories retrieved successfully for category $categoryId"))
    }
}
