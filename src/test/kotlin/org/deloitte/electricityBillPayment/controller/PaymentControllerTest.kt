package org.deloitte.electricityBillPayment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.deloitte.electricityBillPayment.dto.PaymentDto
import org.deloitte.electricityBillPayment.dto.PaymentMethodDto
import org.deloitte.electricityBillPayment.dto.PaymentResponse
import org.deloitte.electricityBillPayment.exception.ApiException
import org.deloitte.electricityBillPayment.exception.ErrorCode
import org.deloitte.electricityBillPayment.config.TestSecurityConfig
import org.deloitte.electricityBillPayment.exception.RestExceptionHandler
import org.deloitte.electricityBillPayment.service.PaymentService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(PaymentController::class)
@Import(RestExceptionHandler::class, TestSecurityConfig::class)
class PaymentControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var paymentService: PaymentService

    private val basePath = "/electricity-bill-payment/v1/payments"

    @Test
    fun `methods endpoint returns ACTIVE methods`() {
        whenever(paymentService.getEnabledMethods()).thenReturn(listOf(PaymentMethodDto(1L, "CARD", "ACTIVE")))

        mockMvc.perform(get("$basePath/methods"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Payment methods retrieved successfully"))
            .andExpect(jsonPath("$.data[0].methodName").value("CARD"))
            .andExpect(jsonPath("$.data[0].status").value("ACTIVE"))
    }

    @Test
    fun `methods endpoint returns empty list when no active methods`() {
        whenever(paymentService.getEnabledMethods()).thenReturn(emptyList())

        mockMvc.perform(get("$basePath/methods"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.data").isEmpty)
    }

    @Test
    fun `process payment success`() {
        val response = PaymentResponse(10L, 1L, "USN1001", 100.0, "CARD", "SUCCESS", LocalDateTime.now(), "Payment processed successfully")
        whenever(paymentService.processPayment(any())).thenReturn(response)

        val body = objectMapper.writeValueAsString(PaymentDto(1L, 1L, 100.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Payment processed successfully"))
            .andExpect(jsonPath("$.data.transactionId").value(10))
            .andExpect(jsonPath("$.data.billId").value(1))
            .andExpect(jsonPath("$.data.billNumber").value("USN1001"))
            .andExpect(jsonPath("$.data.amount").value(100.0))
            .andExpect(jsonPath("$.data.paymentMethod").value("CARD"))
            .andExpect(jsonPath("$.data.status").value("SUCCESS"))
    }

    @Test
    fun `process payment returns 404 when bill not found`() {
        whenever(paymentService.processPayment(any()))
            .thenThrow(ApiException(ErrorCode.NOT_FOUND, "Bill not found with id: 999", HttpStatus.NOT_FOUND))

        val body = objectMapper.writeValueAsString(PaymentDto(999L, 1L, 100.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Bill not found with id: 999"))
    }

    @Test
    fun `process payment returns 404 when payment method not found`() {
        whenever(paymentService.processPayment(any()))
            .thenThrow(ApiException(ErrorCode.NOT_FOUND, "Payment method not found with id: 999", HttpStatus.NOT_FOUND))

        val body = objectMapper.writeValueAsString(PaymentDto(1L, 999L, 100.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Payment method not found with id: 999"))
    }

    @Test
    fun `process payment returns 400 when payment method inactive`() {
        whenever(paymentService.processPayment(any()))
            .thenThrow(ApiException(ErrorCode.VALIDATION_ERROR, "Payment method CARD is not active", HttpStatus.BAD_REQUEST))

        val body = objectMapper.writeValueAsString(PaymentDto(1L, 1L, 100.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Payment method CARD is not active"))
    }

    @Test
    fun `process payment returns 400 when amount mismatch`() {
        whenever(paymentService.processPayment(any()))
            .thenThrow(ApiException(ErrorCode.VALIDATION_ERROR, "Payment amount 100.0 must match total amount due 200.0. Full payment required.", HttpStatus.BAD_REQUEST))

        val body = objectMapper.writeValueAsString(PaymentDto(1L, 1L, 100.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").exists())
    }

    @Test
    fun `process payment returns 400 when request body is empty`() {
        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.errors").isArray)
    }

    @Test
    fun `process payment returns 400 when billId is missing`() {
        val body = """{"paymentMethodId": 1, "amount": 100.0}"""

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.errors").isArray)
    }

    @Test
    fun `process payment returns 400 when paymentMethodId is missing`() {
        val body = """{"billId": 1, "amount": 100.0}"""

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.errors").isArray)
    }

    @Test
    fun `process payment returns 400 when amount is null`() {
        val body = """{"billId": 1, "paymentMethodId": 1}"""

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.errors[0].field").value("amount"))
    }

    @Test
    fun `process payment returns 400 when amount is zero or negative`() {
        val body = objectMapper.writeValueAsString(PaymentDto(1L, 1L, 0.0))

        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.errors").isArray)
    }

    @Test
    fun `process payment returns 400 when malformed JSON`() {
        mockMvc.perform(post(basePath).contentType(MediaType.APPLICATION_JSON).content("{bad"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Malformed JSON request"))
    }
}


