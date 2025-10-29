package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.PaymentDto
import org.deloitte.electricityBillPayment.dto.PaymentMethodDto
import org.deloitte.electricityBillPayment.dto.PaymentResponse
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.service.PaymentService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    private val log = logger<PaymentController>()

    @PostMapping
    fun processPayment(@Valid @RequestBody paymentDto: PaymentDto): ResponseEntity<ApiResponse<PaymentResponse>> {
        log.info("Received payment request for billId: ${paymentDto.billId}")
        
        return try {
            val response = paymentService.processPayment(paymentDto)
            ResponseEntity.ok(response.toSuccessResponse("Payment processed successfully"))
        } catch (ex: BillException) {
            log.error("Bill error during payment processing", ex)
            ResponseEntity.badRequest().body(
                ApiResponse.Error(
                    message = ex.message ?: "Bill not found",
                    code = ErrorCodes.NOT_FOUND
                )
            )
        } catch (ex: IllegalArgumentException) {
            log.warn("Invalid payment request: ${ex.message}")
            ResponseEntity.badRequest().body(
                ApiResponse.Error(
                    message = ex.message ?: "Invalid payment request",
                    code = ErrorCodes.VALIDATION_ERROR
                )
            )
        } catch (ex: Exception) {
            log.error("Unexpected error during payment processing", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to process payment: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @GetMapping("/methods")
    fun getPaymentMethods(): ResponseEntity<ApiResponse<List<PaymentMethodDto>>> {
        log.debug("Fetching available payment methods")
        
        return try {
            val methods = paymentService.getEnabledMethods()
            ResponseEntity.ok(methods.toSuccessResponse("Payment methods retrieved successfully"))
        } catch (ex: Exception) {
            log.error("Error fetching payment methods", ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to fetch payment methods: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }
}
