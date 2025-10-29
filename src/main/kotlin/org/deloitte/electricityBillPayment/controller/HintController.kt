package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.HintDto
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.service.HintService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/hints")
class HintController(private val hintService: HintService) {

    private val log = logger<HintController>()

    @GetMapping
    fun getAllHints(): ResponseEntity<ApiResponse<List<HintDto>>> {
        log.info("Received request to get all security hints")
        
        return try {
            val hints = hintService.getAllHints()
            ResponseEntity.ok(hints.toSuccessResponse("Security hints retrieved successfully"))
        } catch (ex: Exception) {
            log.error("Error while retrieving security hints", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to retrieve security hints: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }
}
