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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/hints")
@Tag(name = "Hint", description = "Hint API operations")
class HintController(private val hintService: HintService) {

    private val log = logger<HintController>()

    @GetMapping
    @Operation(summary = "List security hints", description = "Fetch all security questions/hints")
    fun getAllHints(): ResponseEntity<ApiResponse<List<HintDto>>> {
        log.info("Received request to get all security hints")
        val hints = hintService.getAllHints()
        return ResponseEntity.ok(hints.toSuccessResponse("Security hints retrieved successfully"))
    }
}
