package org.deloitte.electricityBillPayment.entity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "bills")
class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "userID", nullable = false)
    lateinit var userID: User

    @Column(nullable = false)
    lateinit var consumerName: String

    @Column(nullable = false, unique = true)
    lateinit var uniqueServiceNumber: String

    @Column(nullable = false)
    lateinit var serviceNumber: String

    @Column(nullable = false)
    lateinit var eroName: String

    @Column(nullable = false)
    lateinit var address: String

    @Column(nullable = false)
    lateinit var currentMonthBillDate: LocalDate

    @Column(nullable = false)
    var currentMonthBillDateAmount : Double = 0.0

    @Column(nullable = false)
    lateinit var arrearsDate: LocalDate

    @Column(nullable = false)
    var arrearsDateAmount : Double = 0.0

    @Column(nullable = false)
    lateinit var totalAmountDueDate: LocalDate

    @Column(nullable = false)
    var totalAmountDueDateAmount : Double = 0.0

    @Column(nullable = false)
    lateinit var lastMonthPaidDate: LocalDate

    @Column(nullable = false)
    var amountPaidCurrentMonth: Double = 0.0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: BillStatus = BillStatus.PENDING

    @CreationTimestamp
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
}