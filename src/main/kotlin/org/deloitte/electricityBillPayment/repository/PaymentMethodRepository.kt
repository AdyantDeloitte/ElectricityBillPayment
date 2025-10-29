package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.PaymentMethod
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodRepository : JpaRepository<PaymentMethod, Long>