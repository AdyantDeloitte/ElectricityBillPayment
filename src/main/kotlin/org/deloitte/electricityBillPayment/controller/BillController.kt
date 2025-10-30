package org.deloitte.electricityBillPayment.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.BillDto
import org.deloitte.electricityBillPayment.dto.StatusUpdateRequest
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.entity.BillStatus
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.exception.ResourceNotFoundException
import org.deloitte.electricityBillPayment.service.BillService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.MediaType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.deloitte.electricityBillPayment.dto.BillCreateRequest
import org.deloitte.electricityBillPayment.dto.errorResponse
import org.deloitte.electricityBillPayment.dto.ValidationError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as OasApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/bills")
@Validated
@Tag(name = "Bill", description = "Bill API operations")
class BillController(private val billService: BillService) {

    private val log = logger<BillController>()

    @GetMapping("/getBillByUSN/{uniqueServiceNumber}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Get bill by USN", description = "Fetch bill by unique service number")
    @ApiResponses(value = [
        OasApiResponse(responseCode = "200", description = "Bill retrieved successfully", content = [Content(schema = Schema(implementation = org.deloitte.electricityBillPayment.dto.ApiResponse::class))]),
        OasApiResponse(responseCode = "400", description = "Invalid USN", content = [Content()])
    ])
    fun getBillByUSN(
        @PathVariable @Pattern(regexp = "^USN[0-9]+$", message = "USN must start with 'USN' and have digits") uniqueServiceNumber: String
    ): ResponseEntity<ApiResponse<BillDto>> {
        log.debug("Received request to fetch bill for uniqueServiceNumber='{}'", uniqueServiceNumber)
        val regex = Regex("^USN[0-9]+$")
        if (!regex.matches(uniqueServiceNumber)) {
            val errors = listOf(ValidationError(field = "uniqueServiceNumber", message = "USN must start with 'USN' and have digits"))
            val body = errorResponse<BillDto>(message = "Validation failed for one or more fields", errors = errors)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
        }

        val bill = billService.getBillDetails(uniqueServiceNumber)
            ?: throw ResourceNotFoundException("Bill not found for USN $uniqueServiceNumber")
        return ResponseEntity.ok(bill.toDto().toSuccessResponse("Bill retrieved successfully"))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List bills by user", description = "Fetch all bills for a user")
    fun getBillsByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<BillDto>>> {
        log.debug("Fetching bills for user: {}", userId)
        val bills = billService.listBillsByUser(userId)
        val billDtos = bills.map { it.toDto() }
        return ResponseEntity.ok(billDtos.toSuccessResponse("Bills retrieved successfully"))
    }

    @PutMapping("/{billId}/status")
    @Operation(summary = "Update bill status", description = "Update bill status to a new value")
    fun updateBillStatus(
        @PathVariable billId: Long,
        @Valid @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<BillDto>> {
        log.info("Updating bill status for billId: {} to status: {}", billId, statusUpdate.status)
        val status = BillStatus.valueOf(statusUpdate.status.uppercase())
        val updatedBill = billService.updateBillStatus(billId, status)
        val billDto = updatedBill.toDto()
        return ResponseEntity.ok(billDto.toSuccessResponse("Bill status updated successfully"))
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(summary = "Create bill", description = "Create a new bill")
    fun createBill(@Valid @RequestBody request: BillCreateRequest): ResponseEntity<ApiResponse<BillDto>> {
        log.info("Creating bill for USN: {}", request.uniqueServiceNumber)
        val created = billService.createBill(request)
        return ResponseEntity.status(201).body(created.toDto().toSuccessResponse("Bill created successfully"))
    }
}
