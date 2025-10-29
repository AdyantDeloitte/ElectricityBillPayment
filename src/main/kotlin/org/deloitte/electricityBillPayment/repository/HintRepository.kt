package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.Hint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HintRepository: JpaRepository<Hint, Long> {
}