package org.deloitte.electricityBillPayment.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val jwt: JwtProperties = JwtProperties(),
    val database: DatabaseProperties = DatabaseProperties(),
    val security: SecurityProperties = SecurityProperties()
)

data class JwtProperties(
    val secret: String = "",
    val expiration: Long = 86400000L,
    val refreshExpiration: Long = 604800000L
)

data class DatabaseProperties(
    val connectionTimeout: Long = 30000L,
    val maxPoolSize: Int = 10,
    val minPoolSize: Int = 2
)

data class SecurityProperties(
    val cors: CorsProperties = CorsProperties(),
    val rateLimit: RateLimitProperties = RateLimitProperties()
)

data class CorsProperties(
    val allowedOrigins: List<String> = listOf("*"),
    val allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS"),
    val allowedHeaders: List<String> = listOf("*"),
    val allowCredentials: Boolean = true
)

data class RateLimitProperties(
    val enabled: Boolean = true,
    val requestsPerMinute: Int = 100,
    val burstCapacity: Int = 200
)
