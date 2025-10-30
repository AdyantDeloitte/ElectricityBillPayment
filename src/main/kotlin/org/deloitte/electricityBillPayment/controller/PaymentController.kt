package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.PaymentDto
import org.deloitte.electricityBillPayment.dto.PaymentMethodDto
import org.deloitte.electricityBillPayment.dto.PaymentResponse
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.service.PaymentService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as OasApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/payments")
@Tag(name = "Payment", description = "Payment API operations")
class PaymentController(
    private val paymentService: PaymentService
) {

    private val log = logger<PaymentController>()

    @PostMapping
    @Operation(
        summary = "Initiate a payment",
        description = "Creates and processes a payment request."
    )
    @ApiResponses(
        value = [
            OasApiResponse(
                responseCode = "200",
                description = "Payment processed successfully",
                content = [Content(schema = Schema(implementation = org.deloitte.electricityBillPayment.dto.ApiResponse::class))]
            ),
            OasApiResponse(responseCode = "400", description = "Invalid input data", content = [Content()]),
            OasApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    fun processPayment(@Valid @RequestBody paymentDto: PaymentDto): ResponseEntity<ApiResponse<PaymentResponse>> {
        log.info("Received payment request for billId: ${paymentDto.billId}")
        val response = paymentService.processPayment(paymentDto)
        return ResponseEntity.ok(response.toSuccessResponse("Payment processed successfully"))
    }

    @GetMapping("/methods")
    @Operation(
        summary = "List enabled payment methods",
        description = "Fetches available ACTIVE payment methods."
    )
    @ApiResponses(
        value = [
            OasApiResponse(
                responseCode = "200",
                description = "Payment methods retrieved successfully",
                content = [Content(schema = Schema(implementation = org.deloitte.electricityBillPayment.dto.ApiResponse::class))]
            ),
            OasApiResponse(responseCode = "500", description = "Internal server error", content = [Content()])
        ]
    )
    fun getPaymentMethods(): ResponseEntity<ApiResponse<List<PaymentMethodDto>>> {
        log.debug("Fetching available payment methods")
        val methods = paymentService.getEnabledMethods()
        return ResponseEntity.ok(methods.toSuccessResponse("Payment methods retrieved successfully"))
    }
}
