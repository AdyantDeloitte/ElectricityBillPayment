package org.deloitte.electricityBillPayment.service
import org.deloitte.electricityBillPayment.dto.ComplaintRequestDTO
import org.deloitte.electricityBillPayment.dto.ComplaintResponseDTO
import org.deloitte.electricityBillPayment.entity.Complaint
import org.deloitte.electricityBillPayment.exception.ComplaintException
import org.deloitte.electricityBillPayment.exception.UserException
import org.deloitte.electricityBillPayment.entity.ComplaintStatus
import org.deloitte.electricityBillPayment.exception.ComplaintNotFoundException
import org.deloitte.electricityBillPayment.repository.CategoryRepository
import org.deloitte.electricityBillPayment.repository.ComplaintRepository
import org.deloitte.electricityBillPayment.repository.SubCategoryRepository
import org.deloitte.electricityBillPayment.repository.UserRepository
import org.deloitte.electricityBillPayment.util.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ComplaintService(
    private val complaintRepository: ComplaintRepository,
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val userRepository: UserRepository) {

    private val log = logger<ComplaintService>()

    fun registerComplaint(complaintRequestDTO: ComplaintRequestDTO): ComplaintResponseDTO{

        log.info("Logging complaint for consumer number ${complaintRequestDTO.serviceNumber}")

        val category = complaintRequestDTO.categoryId?.let { categoryRepository.findById(it)
            .orElseThrow{
                log.warn("Category not found for id: $it")
                ComplaintException("Category not found for id: $it")
            }
        }
        val subCategory = complaintRequestDTO.subCategoryId?.let { subCategoryRepository.findById(it)
            .orElseThrow {
                log.warn("Sub category not found for id: $it")
                ComplaintException("Sub category not found for id: $it")
            }
        }
        val user = complaintRequestDTO.userId?.let { userRepository.findById(it)
            .orElseThrow {
                log.warn("User not found for id: $it")
                UserException("User not found for id: $it")
            }
        }

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
        log.info("Complaint registered successfully with id: ${savedComplaint.id}")

        return ComplaintResponseDTO(
            id = savedComplaint.id ?: 0L,
            serviceNumber = savedComplaint.serviceNumber,
            subCategory = savedComplaint.subCategory?.name,
            category = savedComplaint.category?.name,
            name = savedComplaint.name,
            email = savedComplaint.email,
            mobile = savedComplaint.mobile,
            status = savedComplaint.status.toString(),
            createdAt = savedComplaint.createdAt.toString()
        )
    }

    @Transactional
    fun updateComplaintStatus(complaintId: Long, status: ComplaintStatus): Complaint {
        log.info("Updating complaint status for complaintId: {} to status: {}", complaintId, status)
        
        val complaint = complaintRepository.findById(complaintId)
            .orElseThrow { ComplaintNotFoundException("Complaint not found with id: $complaintId") }
        
        complaint.status = status
        complaint.updatedAt = LocalDateTime.now()
        
        val updatedComplaint = complaintRepository.save(complaint)
        log.info("Complaint status updated successfully for complaintId: {}", complaintId)
        
        return updatedComplaint
    }

    @Transactional(readOnly = true)
    fun getComplaintsByUser(userId: Long): List<Complaint> {
        log.debug("Fetching complaints for user: {}", userId)
        
        val complaints = complaintRepository.findAll().filter { it.user?.id == userId }
        log.info("Found {} complaints for user: {}", complaints.size, userId)
        
        return complaints
    }
}