package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.Bill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BillRepository : JpaRepository<Bill, Long>{

    fun findByUniqueServiceNumber(uniqueServiceNumber: String): Bill?
    fun existsByUniqueServiceNumber(uniqueServiceNumber: String): Boolean
    fun findAllByUserID_Id(userId: Long): List<Bill>
}
