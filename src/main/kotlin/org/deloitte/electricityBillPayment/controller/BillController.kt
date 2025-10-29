package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.infrastructure.exception.ApiError
import org.deloitte.electricityBillPayment.infrastructure.exception.BillException
import org.deloitte.electricityBillPayment.service.BillService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.jvm.javaClass
import kotlin.text.isNotBlank

@RestController
@RequestMapping("/bill")
class BillController(private val billService: BillService) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/getBillByUSN/{uniqueServiceNumber}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBillByUSN(@PathVariable uniqueServiceNumber: String): ResponseEntity<Any> {

        log.debug("Received request to fetch bill for uniqueServiceNumber='{}'", uniqueServiceNumber)

        return try {
            require(uniqueServiceNumber.isNotBlank()) { "uniqueServiceNumber must not be blank" }

            val bill: Bill? = billService.getBillDetails(uniqueServiceNumber)
            if (bill != null) {
                ResponseEntity.ok(bill)
            } else {
                log.info("Bill not found for uniqueServiceNumber='{}'", uniqueServiceNumber)
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (ex: IllegalArgumentException) {
            log.warn("Bad request for uniqueServiceNumber='{}': {}", uniqueServiceNumber, ex.message)
            val error = ApiError(status = HttpStatus.BAD_REQUEST.value(), message = ex.message ?: "Bad request", timestamp = Instant.now())
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
        } catch (ex: BillException) {
            log.error("Service error while fetching bill for '{}'", uniqueServiceNumber, ex)
            val error = ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = ex.message ?: "Service error", timestamp = Instant.now())
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bill for '{}'", uniqueServiceNumber, ex)
            val error = ApiError(status = HttpStatus.INTERNAL_SERVER_ERROR.value(), message = "Unexpected error", timestamp = Instant.now())
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
        }
    }
}