package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: JpaRepository<Category,Long>{
}