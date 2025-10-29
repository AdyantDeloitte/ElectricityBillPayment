package org.deloitte.electricityBillPayment.controller

import org.deloitte.electricityBillPayment.dto.ComplaintRequestDTO
import org.deloitte.electricityBillPayment.dto.ComplaintResponseDTO
import org.deloitte.electricityBillPayment.service.ComplaintService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/complaints")
class ComplaintController(
    private val complaintService: ComplaintService
)

{
    @PostMapping
    fun registerComplaint(@RequestBody complaintRequestDTO: ComplaintRequestDTO): ResponseEntity<ComplaintResponseDTO>{
        val registerComplaintResponse = complaintService.registerComplaint(complaintRequestDTO)
        return ResponseEntity.ok(registerComplaintResponse)
    }
}