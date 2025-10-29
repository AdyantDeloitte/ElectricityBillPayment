package org.deloitte.electricityBillPayment.service

import org.deloitte.electricityBillPayment.dto.ComplaintRequestDTO
import org.deloitte.electricityBillPayment.dto.ComplaintResponseDTO
import org.deloitte.electricityBillPayment.entity.Complaint
import org.deloitte.electricityBillPayment.repository.CategoryRepository
import org.deloitte.electricityBillPayment.repository.ComplaintRepository
import org.deloitte.electricityBillPayment.repository.SubCategoryRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ComplaintService(
    private val complaintRepository: ComplaintRepository,
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val userRepository: UserRepository) {

    fun registerComplaint(complaintRequestDTO: ComplaintRequestDTO): ComplaintResponseDTO{

        val category = complaintRequestDTO.categoryId?.let { categoryRepository.findById(it).orElseThrow{ Exception("category not found") } }
        val subCategory = complaintRequestDTO.subCategoryId?.let { subCategoryRepository.findById(it).orElseThrow { Exception("sub category not found") } }
        val user = complaintRequestDTO.userId?.let { userRepository.findById(it).orElseThrow { Exception("user not found") }}

        val complaint = Complaint().apply {
            serviceNumber = complaintRequestDTO.serviceNumber
            this.category = category
            this.subCategory = subCategory
            name = complaintRequestDTO.name
            email = complaintRequestDTO.email
            mobile = complaintRequestDTO.mobile
            this.user = user
            documentPath = complaintRequestDTO.documentPath
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }

        val savedComplaint = complaintRepository.save(complaint)

        return ComplaintResponseDTO(
            id = savedComplaint.id ?: 0L,
            serviceNumber = savedComplaint.serviceNumber,
            subCategory = savedComplaint.subCategory?.name,
            category = savedComplaint.category?.name,
            name = savedComplaint.name,
            email = savedComplaint.email,
            mobile = savedComplaint.mobile,
            createdAt = savedComplaint.createdAt.toString()
        )
    }
}