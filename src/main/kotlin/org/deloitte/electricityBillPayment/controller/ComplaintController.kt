package org.deloitte.electricityBillPayment.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.deloitte.electricityBillPayment.dto.ApiResponse
import org.deloitte.electricityBillPayment.dto.ComplaintRequestDTO
import org.deloitte.electricityBillPayment.dto.ComplaintResponseDTO
import org.deloitte.electricityBillPayment.dto.ErrorCodes
import org.deloitte.electricityBillPayment.dto.StatusUpdateRequest
import org.deloitte.electricityBillPayment.dto.toSuccessResponse
import org.deloitte.electricityBillPayment.entity.ComplaintStatus
import org.deloitte.electricityBillPayment.exception.ComplaintNotFoundException
import org.deloitte.electricityBillPayment.mapper.toDto
import org.deloitte.electricityBillPayment.service.ComplaintService
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as OasApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RestController
@RequestMapping("\${app.api.base-path}/\${app.api.version}/complaints")
@Tag(name = "Complaint", description = "Complaint API operations")
class ComplaintController(
    private val complaintService: ComplaintService
) {

    private val log = logger<ComplaintController>()

    @PostMapping
    @Operation(summary = "Register complaint", description = "Create a complaint")
    fun registerComplaint(@Valid @RequestBody complaintRequestDTO: ComplaintRequestDTO): ResponseEntity<ApiResponse<ComplaintResponseDTO>>{
        val registerComplaintResponse = complaintService.registerComplaint(complaintRequestDTO)
        return ResponseEntity.ok(registerComplaintResponse.toSuccessResponse("Complaint registered successfully"))
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "List user complaints", description = "Fetch all complaints for user")
    fun getComplaintsByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<ComplaintResponseDTO>>> {
        log.debug("Fetching complaints for user: {}", userId)
        val complaints = complaintService.getComplaintsByUser(userId)
        val complaintDtos = complaints.map { it.toDto() }
        return ResponseEntity.ok(complaintDtos.toSuccessResponse("Complaints retrieved successfully"))
    }

    @PutMapping("/{complaintId}/status")
    @Operation(summary = "Update complaint status", description = "Update status of a complaint")
    fun updateComplaintStatus(
        @PathVariable complaintId: Long,
        @Valid @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<ComplaintResponseDTO>> {
        log.info("Updating complaint status for complaintId: {} to status: {}", complaintId, statusUpdate.status)
        
        val status = ComplaintStatus.valueOf(statusUpdate.status.uppercase())
        val updatedComplaint = complaintService.updateComplaintStatus(complaintId, status)
        val complaintDto = updatedComplaint.toDto()
        return ResponseEntity.ok(complaintDto.toSuccessResponse("Complaint status updated successfully"))
    }
}