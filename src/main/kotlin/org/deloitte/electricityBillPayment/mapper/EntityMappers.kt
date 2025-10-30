package org.deloitte.electricityBillPayment.mapper

import org.deloitte.electricityBillPayment.dto.UserRegisterResponse
import org.deloitte.electricityBillPayment.entity.Bill
import org.deloitte.electricityBillPayment.entity.User

fun User.toDto() = UserRegisterResponse(
    userId = id,
    userName = username,
    name = name,
    email = email,
    mobile = mobile
)

fun Bill.toDto() = org.deloitte.electricityBillPayment.dto.BillDto(
    id = id,
    consumerName = consumerName,
    uniqueServiceNumber = uniqueServiceNumber,
    serviceNumber = serviceNumber,
    eroName = eroName,
    address = address,
    currentMonthBillDate = currentMonthBillDate,
    currentMonthBillDateAmount = currentMonthBillDateAmount,
    arrearsDate = arrearsDate,
    arrearsDateAmount = arrearsDateAmount,
    totalAmountDueDate = totalAmountDueDate,
    totalAmountDueDateAmount = totalAmountDueDateAmount,
    lastMonthPaidDate = lastMonthPaidDate,
    amountPaidCurrentMonth = amountPaidCurrentMonth,
    status = status.toString(),
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun org.deloitte.electricityBillPayment.entity.PaymentMethod.toDto() =
    org.deloitte.electricityBillPayment.dto.PaymentMethodDto(
        id = id,
        methodName = methodName,
        status = status
    )

fun org.deloitte.electricityBillPayment.entity.Complaint.toDto() =
    org.deloitte.electricityBillPayment.dto.ComplaintResponseDTO(
        id = id ?: 0L,
        serviceNumber = serviceNumber,
        subCategory = subCategory?.name,
        category = category?.name,
        name = name,
        email = email,
        mobile = mobile,
        status = status.toString(),
        createdAt = createdAt.toString()
    )

fun org.deloitte.electricityBillPayment.entity.Hint.toDto() =
    org.deloitte.electricityBillPayment.dto.HintDto(
        id = id,
        question = question
    )

fun org.deloitte.electricityBillPayment.entity.SubCategory.toDto() =
    org.deloitte.electricityBillPayment.dto.SubCategoryDto(
        id = id,
        name = name
    )

fun org.deloitte.electricityBillPayment.entity.Category.toDto() =
    org.deloitte.electricityBillPayment.dto.CategoryDto(
        id = id,
        name = name,
        subcategories = subcategories.map { it.toDto() }
    )