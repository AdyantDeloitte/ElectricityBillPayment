package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.BillDto
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.service.BillService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.text.isNotBlank

@RestController
@RequestMapping("/api/v1/bills")
class BillController(private val billService: BillService) {

    private val log = logger<BillController>()

    @GetMapping("/getBillByUSN/{uniqueServiceNumber}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBillByUSN(@PathVariable uniqueServiceNumber: String): ResponseEntity<ApiResponse<BillDto>> {
        log.debug("Received request to fetch bill for uniqueServiceNumber='{}'", uniqueServiceNumber)

        return try {
            require(uniqueServiceNumber.isNotBlank()) { "uniqueServiceNumber must not be blank" }

            val bill = billService.getBillDetails(uniqueServiceNumber)
            if (bill != null) {
                val billDto = bill.toDto()
                ResponseEntity.ok(billDto.toSuccessResponse("Bill retrieved successfully"))
            } else {
                log.info("Bill not found for uniqueServiceNumber='{}'", uniqueServiceNumber)
                ResponseEntity.notFound().build()
            }
        } catch (ex: IllegalArgumentException) {
            log.warn("Bad request for uniqueServiceNumber='{}': {}", uniqueServiceNumber, ex.message)
            ResponseEntity.badRequest().body(
                ApiResponse.Error(
                    message = ex.message ?: "Bad request",
                    code = ErrorCodes.VALIDATION_ERROR
                )
            )
        } catch (ex: BillException) {
            log.error("Service error while fetching bill for '{}'", uniqueServiceNumber, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = ex.message ?: "Service error",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bill for '{}'", uniqueServiceNumber, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Unexpected error occurred",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @GetMapping("/user/{userId}")
    fun getBillsByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<BillDto>>> {
        log.debug("Fetching bills for user: {}", userId)
        
        return try {
            val bills = billService.listBillsByUser(userId)
            val billDtos = bills.map { it.toDto() }
            ResponseEntity.ok(billDtos.toSuccessResponse("Bills retrieved successfully"))
        } catch (ex: BillException) {
            log.error("Service error while fetching bills for user: {}", userId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = ex.message ?: "Service error",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        } catch (ex: Exception) {
            log.error("Unexpected error while fetching bills for user: {}", userId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Unexpected error occurred",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @PutMapping("/{billId}/status")
    fun updateBillStatus(
        @PathVariable billId: Long,
        @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<BillDto>> {
        log.info("Updating bill status for billId: {} to status: {}", billId, statusUpdate.status)
        
        return try {
            val status = BillStatus.valueOf(statusUpdate.status.uppercase())
            val updatedBill = billService.updateBillStatus(billId, status)
            val billDto = updatedBill.toDto()
            ResponseEntity.ok(billDto.toSuccessResponse("Bill status updated successfully"))
        } catch (ex: IllegalArgumentException) {
            log.warn("Invalid status: {}", statusUpdate.status)
            ResponseEntity.badRequest().body(
                ApiResponse.Error(
                    message = "Invalid status: ${statusUpdate.status}. Valid statuses: PENDING, PAID, OVERDUE, CANCELLED",
                    code = ErrorCodes.VALIDATION_ERROR
                )
            )
        } catch (ex: BillException) {
            log.error("Service error while updating bill status for billId: {}", billId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = ex.message ?: "Service error",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        } catch (ex: Exception) {
            log.error("Unexpected error while updating bill status for billId: {}", billId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Unexpected error occurred",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }
}

data class StatusUpdateRequest(
    val status: String
)