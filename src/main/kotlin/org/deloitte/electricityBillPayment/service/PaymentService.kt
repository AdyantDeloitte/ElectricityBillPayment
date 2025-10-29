package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.PaymentDto
import org.deloitte.electricityBillPayment.dto.PaymentMethodDto
import org.deloitte.electricityBillPayment.dto.PaymentResponse
import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.entity.PaymentMethod
import org.deloitte.electricityBillPayment.entity.PaymentTransaction
import org.deloitte.electricityBillPayment.entity.User
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.exception.InvalidPaymentMethodException
import org.deloitte.electricityBillPayment.exception.PaymentException
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.repository.BillRepository
import org.deloitte.electricityBillPayment.repository.PaymentMethodRepository
import org.deloitte.electricityBillPayment.repository.PaymentTransactionRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentTransactionRepository: PaymentTransactionRepository,
    private val billRepository: BillRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) {

    private val log = logger<PaymentService>()

    @Transactional
    fun processPayment(paymentDto: PaymentDto): PaymentResponse {
        log.info("Processing payment for billId: ${paymentDto.billId}, amount: ${paymentDto.amount}")

        val bill = billRepository.findById(paymentDto.billId)
            .orElseThrow { BillException("Bill not found with id: ${paymentDto.billId}") }

        val paymentMethod = paymentMethodRepository.findById(paymentDto.paymentMethodId)
            .orElseThrow { InvalidPaymentMethodException("Payment method not found with id: ${paymentDto.paymentMethodId}") }

        require(paymentMethod.status == "ACTIVE") { 
            "Payment method ${paymentMethod.methodName} is not active" 
        }

        val totalAmountDue = bill.totalAmountDueDateAmount
        require(paymentDto.amount == totalAmountDue) { 
            "Payment amount ${paymentDto.amount} must match total amount due ${totalAmountDue}. Full payment required." 
        }

        val transaction = PaymentTransaction().apply {
            this.user = bill.userID
            this.bill = bill
            this.paymentMethod = paymentMethod
            this.amount = paymentDto.amount
            this.transactionStatus = "SUCCESS"
            this.remarks = "Full payment processed successfully"
        }

        val savedTransaction = paymentTransactionRepository.save(transaction)
        
        // Update bill status to PAID
        bill.status = BillStatus.PAID
        billRepository.save(bill)
        
        log.info("Payment transaction created with id: ${savedTransaction.id} and bill status updated to PAID")

        return PaymentResponse(
            transactionId = savedTransaction.id ?: 0L,
            billId = bill.id ?: 0L,
            billNumber = bill.uniqueServiceNumber,
            amount = paymentDto.amount,
            paymentMethod = paymentMethod.methodName,
            status = savedTransaction.transactionStatus,
            transactionDate = savedTransaction.transactionDate ?: java.time.LocalDateTime.now(),
            message = "Payment processed successfully"
        )
    }

    @Transactional(readOnly = true)
    fun getEnabledMethods(): List<PaymentMethodDto> {
        log.debug("Fetching all enabled payment methods")
        
        return paymentMethodRepository.findAll()
            .filter { it.status == "ACTIVE" }
            .map { it.toDto() }
    }
}
