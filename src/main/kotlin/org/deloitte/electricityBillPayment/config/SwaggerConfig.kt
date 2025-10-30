package org.deloitte.electricityBillPayment.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Electricity Bill Payment API",
        version = "1.0",
        description = "Handles all payment operations and related resources"
    )
)
@Configuration
class SwaggerConfig


