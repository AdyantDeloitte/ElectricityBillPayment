package org.deloitte.electricityBillPayment.entity

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "payment_transactions")
class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @ManyToOne(optional = false)
    @JoinColumn(name = "bill_id")
    lateinit var bill: Bill

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_method_id")
    lateinit var paymentMethod: PaymentMethod

    @Column(nullable = false)
    var amount: Double=0.0

    var transactionStatus: String = "PENDING"
    var retryCount: Int = 0
    var remarks: String? = null

    @CreationTimestamp
    var transactionDate: LocalDateTime? = null

}