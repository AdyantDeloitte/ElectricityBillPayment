package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.PaymentDto
import org.deloitte.electricityBillPayment.dto.PaymentMethodDto
import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.entity.PaymentMethod
import org.deloitte.electricityBillPayment.entity.PaymentTransaction
import org.deloitte.electricityBillPayment.entity.User
import org.deloitte.electricityBillPayment.exception.ApiException
import org.deloitte.electricityBillPayment.repository.BillRepository
import org.deloitte.electricityBillPayment.repository.PaymentMethodRepository
import org.deloitte.electricityBillPayment.repository.PaymentTransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class PaymentServiceTest {

    @Mock
    lateinit var paymentTransactionRepository: PaymentTransactionRepository

    @Mock
    lateinit var billRepository: BillRepository

    @Mock
    lateinit var paymentMethodRepository: PaymentMethodRepository

    @InjectMocks
    lateinit var paymentService: PaymentService

    private fun buildUser(): User = User().apply {
        id = 10L
        username = "john"
        name = "John Doe"
        email = "john@example.com"
        mobile = "9999999999"
        password = "hashed"
    }

    private fun buildBill(user: User, totalDue: Double = 100.0): Bill = Bill().apply {
        id = 20L
        userID = user
        consumerName = "John Doe"
        uniqueServiceNumber = "USN-123"
        serviceNumber = "SRV-1"
        eroName = "Bhopal"
        address = "Bhopal"
        currentMonthBillDate = LocalDate.now()
        currentMonthBillDateAmount = 50.0
        arrearsDate = LocalDate.now()
        arrearsDateAmount = 50.0
        totalAmountDueDate = LocalDate.now()
        totalAmountDueDateAmount = totalDue
        lastMonthPaidDate = LocalDate.now()
        amountPaidCurrentMonth = 0.0
        status = BillStatus.PENDING
    }

    private fun buildPaymentMethod(status: String = "ACTIVE"): PaymentMethod = PaymentMethod().apply {
        id = 30L
        methodName = "CARD"
        this.status = status
    }

    @Test
    fun `processPayment should succeed when inputs are valid and full amount`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0)
        val paymentMethod = buildPaymentMethod(status = "ACTIVE")
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        val savedTx = PaymentTransaction().apply {
            id = 99L
            transactionStatus = "SUCCESS"
            transactionDate = LocalDateTime.now()
        }
        `when`(paymentTransactionRepository.save(org.mockito.Mockito.any(PaymentTransaction::class.java))).thenReturn(savedTx)
        `when`(billRepository.save(org.mockito.Mockito.any(Bill::class.java))).thenReturn(bill)

        val response = paymentService.processPayment(dto)

        assertNotNull(response)
        assertEquals(99L, response.transactionId)
        assertEquals(bill.id!!, response.billId)
        assertEquals("USN-123", response.billNumber)
        assertEquals(100.0, response.amount)
        assertEquals("CARD", response.paymentMethod)
        assertEquals("SUCCESS", response.status)
        assertNotNull(response.transactionDate)

        assertEquals(BillStatus.PAID, bill.status)
        verify(paymentTransactionRepository).save(org.mockito.Mockito.any(PaymentTransaction::class.java))
        verify(billRepository).save(org.mockito.Mockito.any(Bill::class.java))
    }

    @Test
    fun `processPayment should throw when bill not found`() {
        val dto = PaymentDto(billId = 1L, paymentMethodId = 2L, amount = 100.0)
        `when`(billRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows(ApiException::class.java) {
            paymentService.processPayment(dto)
        }
    }

    @Test
    fun `processPayment should throw when payment method not found`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0)
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = 999L, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(ApiException::class.java) {
            paymentService.processPayment(dto)
        }
    }

    @Test
    fun `processPayment should throw when payment method inactive`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0)
        val paymentMethod = buildPaymentMethod(status = "INACTIVE")
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        assertThrows(ApiException::class.java) {
            paymentService.processPayment(dto)
        }
    }

    @Test
    fun `processPayment should throw when amount does not match total due`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 200.0)
        val paymentMethod = buildPaymentMethod(status = "ACTIVE")
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        assertThrows(ApiException::class.java) {
            paymentService.processPayment(dto)
        }
    }

    @Test
    fun `getEnabledMethods should return only ACTIVE methods mapped to dto`() {
        val active = buildPaymentMethod("ACTIVE").apply { id = 1L; methodName = "CARD" }
        val inactive = buildPaymentMethod("INACTIVE").apply { id = 2L; methodName = "NETBANKING" }
        `when`(paymentMethodRepository.findAll()).thenReturn(listOf(active, inactive))

        val result: List<PaymentMethodDto> = paymentService.getEnabledMethods()

        assertEquals(1, result.size)
        assertEquals(1L, result.first().id)
        assertEquals("CARD", result.first().methodName)
        assertEquals("ACTIVE", result.first().status)
    }

    @Test
    fun `getEnabledMethods should return empty list when no active methods exist`() {
        val inactive1 = buildPaymentMethod("INACTIVE").apply { id = 1L; methodName = "CARD" }
        val inactive2 = buildPaymentMethod("INACTIVE").apply { id = 2L; methodName = "NETBANKING" }
        `when`(paymentMethodRepository.findAll()).thenReturn(listOf(inactive1, inactive2))

        val result: List<PaymentMethodDto> = paymentService.getEnabledMethods()

        assertEquals(0, result.size)
    }

    @Test
    fun `getEnabledMethods should return empty list when repository returns empty`() {
        `when`(paymentMethodRepository.findAll()).thenReturn(emptyList())

        val result: List<PaymentMethodDto> = paymentService.getEnabledMethods()

        assertEquals(0, result.size)
    }

    @Test
    fun `processPayment should handle null transaction id with fallback to zero`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0)
        val paymentMethod = buildPaymentMethod(status = "ACTIVE")
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        val savedTx = PaymentTransaction().apply {
            id = null
            transactionStatus = "SUCCESS"
            transactionDate = LocalDateTime.now()
        }
        `when`(paymentTransactionRepository.save(org.mockito.Mockito.any(PaymentTransaction::class.java))).thenReturn(savedTx)
        `when`(billRepository.save(org.mockito.Mockito.any(Bill::class.java))).thenReturn(bill)

        val response = paymentService.processPayment(dto)

        assertEquals(0L, response.transactionId)
    }

    @Test
    fun `processPayment should handle null bill id with fallback to zero`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0).apply { id = null }
        val paymentMethod = buildPaymentMethod(status = "ACTIVE")
        val dto = PaymentDto(billId = 0L, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(0L)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        val savedTx = PaymentTransaction().apply {
            id = 99L
            transactionStatus = "SUCCESS"
            transactionDate = LocalDateTime.now()
        }
        `when`(paymentTransactionRepository.save(org.mockito.Mockito.any(PaymentTransaction::class.java))).thenReturn(savedTx)
        `when`(billRepository.save(org.mockito.Mockito.any(Bill::class.java))).thenReturn(bill)

        val response = paymentService.processPayment(dto)

        assertEquals(0L, response.billId)
    }

    @Test
    fun `processPayment should handle null transaction date with fallback to now`() {
        val user = buildUser()
        val bill = buildBill(user, totalDue = 100.0)
        val paymentMethod = buildPaymentMethod(status = "ACTIVE")
        val dto = PaymentDto(billId = bill.id!!, paymentMethodId = paymentMethod.id!!, amount = 100.0)

        `when`(billRepository.findById(bill.id!!)).thenReturn(Optional.of(bill))
        `when`(paymentMethodRepository.findById(paymentMethod.id!!)).thenReturn(Optional.of(paymentMethod))

        val savedTx = PaymentTransaction().apply {
            id = 99L
            transactionDate = null
        }
        `when`(paymentTransactionRepository.save(org.mockito.Mockito.any(PaymentTransaction::class.java))).thenReturn(savedTx)
        `when`(billRepository.save(org.mockito.Mockito.any(Bill::class.java))).thenReturn(bill)

        val response = paymentService.processPayment(dto)

        assertNotNull(response.transactionDate)
    }
}


