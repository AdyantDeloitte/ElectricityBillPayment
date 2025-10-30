package org.deloitte.electricityBillPayment.service


import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.exception.ApiException
import org.deloitte.electricityBillPayment.exception.ErrorCode
import org.deloitte.electricityBillPayment.dto.BillCreateRequest
import org.deloitte.electricityBillPayment.mapper.toEntity
import org.deloitte.electricityBillPayment.repository.BillRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.text.isNotBlank

@Service
class BillService(
    private val repository: BillRepository,
    private val userRepository: UserRepository
) {

    private val log = logger<BillService>()

    @Transactional(readOnly = true)
    fun getBillDetails(uniqueServiceNumber: String): Bill? {
        require(uniqueServiceNumber.isNotBlank()) { "uniqueServiceNumber must not be blank" }
        log.debug("Looking up bill by uniqueServiceNumber='{}'", uniqueServiceNumber)
        return repository.findByUniqueServiceNumber(uniqueServiceNumber)
    }

    @Transactional
    fun updateBillStatus(billId: Long, status: BillStatus): Bill {
        log.info("Updating bill status for billId: {} to status: {}", billId, status)
        val bill = repository.findById(billId)
            .orElseThrow { BillException("Bill not found with id: $billId") }
        bill.status = status
        return repository.save(bill)
    }

    @Transactional(readOnly = true)
    fun listBillsByUser(userId: Long): List<Bill> {
        log.debug("Fetching bills for user: {}", userId)
        val bills = repository.findAll().filter { it.userID.id == userId }
        log.info("Found {} bills for user: {}", bills.size, userId)
        return bills
    }

    fun getBillsByUserId(userId: Long): List<Bill> = repository.findAllByUserID_Id(userId)

    @Transactional
    fun createBill(request: BillCreateRequest): Bill {
        if (repository.existsByUniqueServiceNumber(request.uniqueServiceNumber)) {
            throw ApiException(ErrorCode.CONFLICT, "Bill with uniqueServiceNumber already exists", HttpStatus.CONFLICT)
        }
        val user = userRepository.findById(requireNotNull(request.userId) { "userId must not be null" })
            .orElseThrow { ApiException(ErrorCode.NOT_FOUND, "User not found with id: ${request.userId}", HttpStatus.NOT_FOUND) }
        val entity = request.toEntity(user)
        return repository.save(entity)
    }
}