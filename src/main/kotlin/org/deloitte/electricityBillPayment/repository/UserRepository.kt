package org.deloitte.electricityBillPayment.repository

import org.deloitte.electricityBillPayment.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
    fun existsByMobile(mobile: String): Boolean
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
}