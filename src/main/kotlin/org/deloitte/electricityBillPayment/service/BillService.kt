package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.repository.BillRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.javaClass
import kotlin.text.isNotBlank


@Service
class BillService(private val repository: BillRepository){

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getBillDetails(uniqueServiceNumber: String):Bill? {

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
            throw BillException(
                "Failed to fetch bill for $uniqueServiceNumber",
                ex
            )
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bill for '{}'", uniqueServiceNumber, ex)
            throw BillException("Unexpected error while fetching bill for $uniqueServiceNumber", ex)
        }

    }


}