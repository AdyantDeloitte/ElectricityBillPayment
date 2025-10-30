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

@RestController
@RequestMapping("/api/v1/complaints")
class ComplaintController(
    private val complaintService: ComplaintService
) {

    private val log = logger<ComplaintController>()

    @PostMapping
    fun registerComplaint(@Valid @RequestBody complaintRequestDTO: ComplaintRequestDTO): ResponseEntity<ComplaintResponseDTO>{
        val registerComplaintResponse = complaintService.registerComplaint(complaintRequestDTO)
        return ResponseEntity.ok(registerComplaintResponse)
    }

    @GetMapping("/user/{userId}")
    fun getComplaintsByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<List<ComplaintResponseDTO>>> {
        log.debug("Fetching complaints for user: {}", userId)
        
        return try {
            val complaints = complaintService.getComplaintsByUser(userId)
            val complaintDtos = complaints.map { it.toDto() }
            ResponseEntity.ok(complaintDtos.toSuccessResponse("Complaints retrieved successfully"))
        } catch (ex: Exception) {
            log.error("Error while fetching complaints for user: {}", userId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Failed to retrieve complaints: ${ex.message}",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }

    @PutMapping("/{complaintId}/status")
    fun updateComplaintStatus(
        @PathVariable complaintId: Long,
        @Valid @RequestBody statusUpdate: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<ComplaintResponseDTO>> {
        log.info("Updating complaint status for complaintId: {} to status: {}", complaintId, statusUpdate.status)
        
        return try {
            val status = ComplaintStatus.valueOf(statusUpdate.status.uppercase())
            val updatedComplaint = complaintService.updateComplaintStatus(complaintId, status)
            val complaintDto = updatedComplaint.toDto()
            ResponseEntity.ok(complaintDto.toSuccessResponse("Complaint status updated successfully"))
        } catch (ex: IllegalArgumentException) {
            log.warn("Invalid status: {}", statusUpdate.status)
            ResponseEntity.badRequest().body(
                ApiResponse.Error(
                    message = "Invalid status: ${statusUpdate.status}. Valid statuses: OPEN, IN_PROGRESS, RESOLVED, CLOSED, REJECTED",
                    code = ErrorCodes.VALIDATION_ERROR
                )
            )
        } catch (ex: ComplaintNotFoundException) {
            log.warn("Complaint not found: {}", complaintId)
            ResponseEntity.notFound().build()
        } catch (ex: Exception) {
            log.error("Unexpected error while updating complaint status for complaintId: {}", complaintId, ex)
            ResponseEntity.internalServerError().body(
                ApiResponse.Error(
                    message = "Unexpected error occurred",
                    code = ErrorCodes.INTERNAL_SERVER_ERROR
                )
            )
        }
    }
}