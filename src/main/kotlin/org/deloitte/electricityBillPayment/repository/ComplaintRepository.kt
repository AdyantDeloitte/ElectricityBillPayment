package org.deloitte.electricityBillPayment.repository
import org.deloitte.electricityBillPayment.entity.Complaint
import org.springframework.data.jpa.repository.JpaRepository

interface ComplaintRepository: JpaRepository<Complaint, Long>{
}