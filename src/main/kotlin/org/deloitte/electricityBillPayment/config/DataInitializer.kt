package org.deloitte.electricityBillPayment.config

import org.deloitte.electricityBillPayment.entity.*
import org.deloitte.electricityBillPayment.repository.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DataInitializer(
    private val hintRepository: HintRepository,
    private val categoryRepository: CategoryRepository,
    private val subCategoryRepository: SubCategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val userRepository: UserRepository,
    private val billRepository: BillRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (hintRepository.count() == 0L) {
            log.info("Initializing sample data...")
            initializeHints()
            initializeCategories()
            initializePaymentMethods()
            initializeUsers()
            initializeBills()
            log.info("Sample data initialization completed!")
        } else {
            log.info("Sample data already exists, skipping initialization.")
        }
    }

    private fun initializeHints() {
        val hints = listOf(
            "What is your mother's maiden name?",
            "What was the name of your first pet?",
            "What city were you born in?",
            "What was your childhood nickname?",
            "What is the name of your favorite teacher?",
            "What was the make of your first car?",
            "What is your favorite sport?",
            "What is the name of your best friend?"
        )

        hints.forEach { question ->
            val hint = Hint()
            hint.question = question
            hintRepository.save(hint)
        }
        log.info("Inserted ${hints.size} security hints")
    }

    private fun initializeCategories() {
        val categories = mapOf(
            "Billing Issue" to listOf("Wrong Amount", "Duplicate Bill", "Late Fee Dispute", "Meter Reading Error"),
            "Service Disruption" to listOf("Power Outage", "Voltage Fluctuation", "Frequent Tripping", "Low Voltage"),
            "Connection Related" to listOf("New Connection", "Disconnection Request", "Transfer Connection", "Load Enhancement"),
            "Meter Issue" to listOf("Meter Not Working", "Meter Replacement", "Fast Meter", "Slow Meter"),
            "Payment Issue" to listOf("Payment Not Reflected", "Refund Request", "Payment Gateway Error", "Failed Transaction")
        )

        categories.forEach { (categoryName, subCategories) ->
            val category = Category().apply {
                name = categoryName
            }
            val savedCategory = categoryRepository.save(category)

            subCategories.forEach { subCatName ->
                val subCategory = SubCategory()
                subCategory.name = subCatName
                subCategory.category = savedCategory
                subCategoryRepository.save(subCategory)
            }
        }
        log.info("Inserted ${categories.size} categories with subcategories")
    }

    private fun initializePaymentMethods() {
        val paymentMethods = listOf(
            "Razorpay",
            "payU",
            "Stripe",
        )

        paymentMethods.forEach { methodName ->
            val paymentMethod = PaymentMethod().apply {
                this.methodName = methodName
                status = "ACTIVE"
            }
            paymentMethodRepository.save(paymentMethod)
        }
        log.info("Inserted ${paymentMethods.size} payment methods")
    }

    private fun initializeUsers() {
        val hints = hintRepository.findAll()
        if (hints.isEmpty()) return

        val users = listOf(
            UserData(
                username = "rajesh_kumar",
                name = "Rajesh Kumar",
                email = "rajesh.kumar@email.com",
                mobile = "9876543210",
                password = "SecurePass123",
                hintId = hints[0].id!!,
                hintAnswer = "sharma"
            ),
            UserData(
                username = "priya_sharma",
                name = "Priya Sharma",
                email = "priya.sharma@email.com",
                mobile = "9876543211",
                password = "SecurePass456",
                hintId = hints[1].id!!,
                hintAnswer = "tommy"
            ),
            UserData(
                username = "amit_patel",
                name = "Amit Patel",
                email = "amit.patel@email.com",
                mobile = "9876543212",
                password = "SecurePass789",
                hintId = hints[2].id!!,
                hintAnswer = "bhopal"
            ),
            UserData(
                username = "sunita_verma",
                name = "Sunita Verma",
                email = "sunita.verma@email.com",
                mobile = "9876543213",
                password = "SecurePass321",
                hintId = hints[3].id!!,
                hintAnswer = "chotu"
            ),
            UserData(
                username = "vikram_singh",
                name = "Vikram Singh",
                email = "vikram.singh@email.com",
                mobile = "9876543214",
                password = "SecurePass654",
                hintId = hints[4].id!!,
                hintAnswer = "mrs_sharma"
            )
        )

        users.forEach { userData ->
            if (!userRepository.existsByEmail(userData.email) &&
                !userRepository.existsByUsername(userData.username) &&
                !userRepository.existsByMobile(userData.mobile)
            ) {
                val hint = hintRepository.findById(userData.hintId).orElse(null)
                if (hint != null) {
                    val user = User().apply {
                        username = userData.username
                        name = userData.name
                        email = userData.email
                        mobile = userData.mobile
                        password = passwordEncoder.encode(userData.password)
                        this.hint = hint
                        hintAnswer = userData.hintAnswer.lowercase()
                        createdAt = LocalDateTime.now()
                        updatedAt = LocalDateTime.now()
                    }
                    userRepository.save(user)
                }
            }
        }
        log.info("Inserted ${users.size} sample users")
    }

    private fun initializeBills() {
        val users = userRepository.findAll()
        if (users.isEmpty()) return

        val bills = listOf(
            BillData(
                username = "rajesh_kumar",
                consumerName = "Rajesh Kumar",
                uniqueServiceNumber = "USN2024123456789",
                serviceNumber = "SN123456789",
                eroName = "ERO Bhopal Central",
                address = "House No. 45, Sector 5, Bhopal, Madhya Pradesh - 462001",
                currentMonthBillDate = LocalDate.of(2024, 12, 15),
                currentMonthBillAmount = 1850.50,
                arrearsDate = LocalDate.of(2024, 11, 15),
                arrearsAmount = 320.00,
                totalAmountDueDate = LocalDate.of(2024, 12, 30),
                totalAmountDue = 2170.50,
                lastMonthPaidDate = LocalDate.of(2024, 11, 28),
                amountPaidCurrentMonth = 1850.50
            ),
            BillData(
                username = "priya_sharma",
                consumerName = "Priya Sharma",
                uniqueServiceNumber = "USN2024123456790",
                serviceNumber = "SN123456790",
                eroName = "ERO Bhopal South",
                address = "Flat 302, Green Valley Apartments, Bhopal, Madhya Pradesh - 462002",
                currentMonthBillDate = LocalDate.of(2024, 12, 18),
                currentMonthBillAmount = 2450.75,
                arrearsDate = LocalDate.of(2024, 11, 18),
                arrearsAmount = 0.00,
                totalAmountDueDate = LocalDate.of(2025, 1, 5),
                totalAmountDue = 2450.75,
                lastMonthPaidDate = LocalDate.of(2024, 11, 15),
                amountPaidCurrentMonth = 2450.75
            ),
            BillData(
                username = "amit_patel",
                consumerName = "Amit Patel",
                uniqueServiceNumber = "USN2024123456791",
                serviceNumber = "SN123456791",
                eroName = "ERO Bhopal North",
                address = "Shop No. 12, Commercial Complex, Bhopal, Madhya Pradesh - 462003",
                currentMonthBillDate = LocalDate.of(2024, 12, 20),
                currentMonthBillAmount = 3250.00,
                arrearsDate = LocalDate.of(2024, 11, 20),
                arrearsAmount = 450.25,
                totalAmountDueDate = LocalDate.of(2025, 1, 10),
                totalAmountDue = 3700.25,
                lastMonthPaidDate = LocalDate.of(2024, 11, 22),
                amountPaidCurrentMonth = 3250.00
            ),
            BillData(
                username = "sunita_verma",
                consumerName = "Sunita Verma",
                uniqueServiceNumber = "USN2024123456792",
                serviceNumber = "SN123456792",
                eroName = "ERO Bhopal East",
                address = "Villa 8, Royal Gardens, Bhopal, Madhya Pradesh - 462004",
                currentMonthBillDate = LocalDate.of(2024, 12, 22),
                currentMonthBillAmount = 4200.00,
                arrearsDate = LocalDate.of(2024, 11, 22),
                arrearsAmount = 0.00,
                totalAmountDueDate = LocalDate.of(2025, 1, 15),
                totalAmountDue = 4200.00,
                lastMonthPaidDate = LocalDate.of(2024, 11, 25),
                amountPaidCurrentMonth = 4200.00
            ),
            BillData(
                username = "vikram_singh",
                consumerName = "Vikram Singh",
                uniqueServiceNumber = "USN2024123456793",
                serviceNumber = "SN123456793",
                eroName = "ERO Bhopal West",
                address = "Building A-5, Tech Park, Bhopal, Madhya Pradesh - 462005",
                currentMonthBillDate = LocalDate.of(2024, 12, 25),
                currentMonthBillAmount = 3150.50,
                arrearsDate = LocalDate.of(2024, 11, 25),
                arrearsAmount = 125.50,
                totalAmountDueDate = LocalDate.of(2025, 1, 20),
                totalAmountDue = 3276.00,
                lastMonthPaidDate = LocalDate.of(2024, 11, 28),
                amountPaidCurrentMonth = 3150.50
            )
        )

        bills.forEach { billData ->
            val user = users.find { it.username == billData.username }
            if (user != null && !billRepository.existsByUniqueServiceNumber(billData.uniqueServiceNumber)) {
                val bill = Bill().apply {
                    userID = user
                    consumerName = billData.consumerName
                    uniqueServiceNumber = billData.uniqueServiceNumber
                    serviceNumber = billData.serviceNumber
                    eroName = billData.eroName
                    address = billData.address
                    currentMonthBillDate = billData.currentMonthBillDate
                    currentMonthBillDateAmount = billData.currentMonthBillAmount
                    arrearsDate = billData.arrearsDate
                    arrearsDateAmount = billData.arrearsAmount
                    totalAmountDueDate = billData.totalAmountDueDate
                    totalAmountDueDateAmount = billData.totalAmountDue
                    lastMonthPaidDate = billData.lastMonthPaidDate
                    amountPaidCurrentMonth = billData.amountPaidCurrentMonth
                    createdAt = LocalDateTime.now()
                    updatedAt = LocalDateTime.now()
                }
                billRepository.save(bill)
            }
        }
        log.info("Inserted ${bills.size} sample bills")
    }

    private data class UserData(
        val username: String,
        val name: String,
        val email: String,
        val mobile: String,
        val password: String,
        val hintId: Long,
        val hintAnswer: String
    )

    private data class BillData(
        val username: String,
        val consumerName: String,
        val uniqueServiceNumber: String,
        val serviceNumber: String,
        val eroName: String,
        val address: String,
        val currentMonthBillDate: LocalDate,
        val currentMonthBillAmount: Double,
        val arrearsDate: LocalDate,
        val arrearsAmount: Double,
        val totalAmountDueDate: LocalDate,
        val totalAmountDue: Double,
        val lastMonthPaidDate: LocalDate,
        val amountPaidCurrentMonth: Double
    )
}
