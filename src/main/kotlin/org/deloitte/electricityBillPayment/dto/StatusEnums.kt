package org.deloitte.electricityBillPayment.dto

sealed class PaymentStatus {
    object PENDING : PaymentStatus()
    object SUCCESS : PaymentStatus()
    object FAILED : PaymentStatus()
    object CANCELLED : PaymentStatus()
    
    override fun toString(): String = when (this) {
        is PENDING -> "PENDING"
        is SUCCESS -> "SUCCESS"
        is FAILED -> "FAILED"
        is CANCELLED -> "CANCELLED"
    }
}

sealed class BillStatus {
    object PENDING : BillStatus()
    object PAID : BillStatus()
    object OVERDUE : BillStatus()
    object CANCELLED : BillStatus()
    
    override fun toString(): String = when (this) {
        is PENDING -> "PENDING"
        is PAID -> "PAID"
        is OVERDUE -> "OVERDUE"
        is CANCELLED -> "CANCELLED"
    }
}

sealed class ComplaintStatus {
    object OPEN : ComplaintStatus()
    object IN_PROGRESS : ComplaintStatus()
    object RESOLVED : ComplaintStatus()
    object CLOSED : ComplaintStatus()
    
    override fun toString(): String = when (this) {
        is OPEN -> "OPEN"
        is IN_PROGRESS -> "IN_PROGRESS"
        is RESOLVED -> "RESOLVED"
        is CLOSED -> "CLOSED"
    }
}
