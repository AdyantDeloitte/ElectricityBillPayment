package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.PaymentTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentTransactionRepository : JpaRepository<PaymentTransaction, Long>