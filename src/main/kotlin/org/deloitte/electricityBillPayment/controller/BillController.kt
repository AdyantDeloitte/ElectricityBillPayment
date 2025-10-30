package org.deloitte.electricityBillPayment.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.BillDto
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.StatusUpdateRequest
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.exception.BillException
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.service.BillService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/bills")
@Validated
class BillController(private val billService: BillService) {

    private val log = logger<BillController>()

    @GetMapping("/getBillByUSN/{uniqueServiceNumber}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBillByUSN(
        @PathVariable uniqueServiceNumber: String
    ): ResponseEntity<ApiResponse<BillDto>> {
        log.debug("Received request to fetch bill for uniqueServiceNumber='{}'", uniqueServiceNumber)
        return try {
            val usnPattern = Regex("^USN[0-9]+$")
            if (!usnPattern.matches(uniqueServiceNumber)) {
                log.warn("Invalid uniqueServiceNumber format: {}", uniqueServiceNumber)
                return ResponseEntity.badRequest().body(
                    ApiResponse.Error(
                        message = "Invalid uniqueServiceNumber format",
                        details = "Must start with 'USN' and contain only numeric characters",
                        code = ErrorCodes.VALIDATION_ERROR
                    )
                )
            }

            val bill = billService.getBillDetails(uniqueServiceNumber)
            if (bill != null) {
                ResponseEntity.ok(bill.toDto().toSuccessResponse("Bill retrieved successfully"))
            } else {
                log.info("Bill not found for uniqueServiceNumber='{}'", uniqueServiceNumber)
                ResponseEntity.ok(
                    ApiResponse.Error(
                        message = "Bill not found",
                        details = "Bill not found for Unique Service Number='$uniqueServiceNumber'",
                        code = ErrorCodes.NOT_FOUND
                    )
                )
            }
        } catch (ex: BillException) {
            log.error("Service error while fetching bill for uniqueServiceNumber='{}'", uniqueServiceNumber, ex)
            return ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = ex.message ?: "Service error",
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
        }
    }

    @PutMapping("/{billId}/status")
    fun updateBillStatus(
        @PathVariable billId: Long,
        @Valid @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<BillDto>> {
        log.info("Updating bill status for billId: {} to status: {}", billId, statusUpdate.status)

        return try {
            val status = BillStatus.valueOf(statusUpdate.status.uppercase())
            val updatedBill = billService.updateBillStatus(billId, status)
            val billDto = updatedBill.toDto()
            ResponseEntity.ok(billDto.toSuccessResponse("Bill status updated successfully"))
        } catch (ex: BillException) {
            log.error("Service error while updating bill status for billId: {}", billId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = ex.message ?: "Service error",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @GetMapping("/getBillByUSN/")
    fun getBillByUSNMissing(): ResponseEntity<ApiResponse<*>> {
        log.warn("Missing uniqueServiceNumber in request to getBillByUSN")
        return ResponseEntity.badRequest().body(
            ApiResponse.Error(
                message = "Enter uniqueServiceNumber",
                details = "uniqueServiceNumber is missing",
                code = ErrorCodes.VALIDATION_ERROR
            )
        )
    }
}
