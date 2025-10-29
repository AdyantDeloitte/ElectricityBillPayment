package org.deloitte.electricityBillPayment.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun String.isValidEmail(): Boolean = 
    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex().matches(this)

fun String.isValidMobile(): Boolean = 
    "^[0-9]{10}$".toRegex().matches(this)

fun String.isValidUsername(): Boolean = 
    "^[a-zA-Z0-9_]{3,50}$".toRegex().matches(this)

fun String.sanitize(): String = this.trim().replace("\\s+".toRegex(), " ")
