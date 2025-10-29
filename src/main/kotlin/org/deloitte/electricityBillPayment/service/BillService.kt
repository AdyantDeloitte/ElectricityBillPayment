package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.repository.BillRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.text.isNotBlank

@Service
class BillService(private val repository: BillRepository) {

    private val log = logger<BillService>()

    @Transactional(readOnly = true)
    fun getBillDetails(uniqueServiceNumber: String): Bill? {
        require(uniqueServiceNumber.isNotBlank()) { "uniqueServiceNumber must not be blank" }
        log.debug("Looking up bill by uniqueServiceNumber='{}'", uniqueServiceNumber)

        return try {
            val bill = repository.findByUniqueServiceNumber(uniqueServiceNumber)
            if (bill == null) {
                log.info("Bill not found for uniqueServiceNumber='{}'", uniqueServiceNumber)
            }
            bill
        } catch (ex: DataAccessException) {
            log.error("Database error while fetching bill for '{}'", uniqueServiceNumber, ex)
            throw BillException("Failed to fetch bill for $uniqueServiceNumber", ex)
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bill for '{}'", uniqueServiceNumber, ex)
            throw BillException("Unexpected error while fetching bill for $uniqueServiceNumber", ex)
        }
    }

    @Transactional
    fun updateBillStatus(billId: Long, status: BillStatus): Bill {
        log.info("Updating bill status for billId: {} to status: {}", billId, status)
        
        return try {
            val bill = repository.findById(billId)
                .orElseThrow { BillException("Bill not found with id: $billId") }
            
            bill.status = status
            val updatedBill = repository.save(bill)
            
            log.info("Bill status updated successfully for billId: {}", billId)
            updatedBill
        } catch (ex: DataAccessException) {
            log.error("Database error while updating bill status for billId: {}", billId, ex)
            throw BillException("Failed to update bill status for $billId", ex)
        } catch (ex: Exception) {
            log.error("Unexpected error while updating bill status for billId: {}", billId, ex)
            throw BillException("Unexpected error while updating bill status for $billId", ex)
        }
    }

    @Transactional(readOnly = true)
    fun listBillsByUser(userId: Long): List<Bill> {
        log.debug("Fetching bills for user: {}", userId)
        
        return try {
            val bills = repository.findAll().filter { it.userID.id == userId }
            log.info("Found {} bills for user: {}", bills.size, userId)
            bills
        } catch (ex: DataAccessException) {
            log.error("Database error while fetching bills for user: {}", userId, ex)
            throw BillException("Failed to fetch bills for user $userId", ex)
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bills for user: {}", userId, ex)
            throw BillException("Unexpected error while fetching bills for user $userId", ex)
        }
    }
}