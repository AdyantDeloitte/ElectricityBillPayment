package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.SubCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubCategoryRepository: JpaRepository<SubCategory, Long> {
}