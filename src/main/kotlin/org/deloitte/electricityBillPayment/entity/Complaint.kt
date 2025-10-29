package org.deloitte.electricityBillPayment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "complaints")
class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    lateinit var serviceNumber: String

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    var subCategory: SubCategory? = null

    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: Category? = null

    @Column(nullable = false)
    lateinit var name: String

    @Column(nullable = false)
    lateinit var email: String

    @Column(nullable = false)
    lateinit var mobile: String

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null

    @Column(name= "document_path")
    var documentPath: String? = null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ComplaintStatus = ComplaintStatus.OPEN

    @CreationTimestamp
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null
}