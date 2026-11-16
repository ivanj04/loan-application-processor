package com.ivanjiang.loanprocessor.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Loan Application Processor API",
        version = "1.0",
        description = "Spring Batch mortgage loan processing system — " +
                      "ingests CSV loan files, validates, scores by LTV/income/credit, " +
                      "classifies, and persists results. Modeled on Freddie Mac batch processing workflows.",
        contact = @Contact(name = "Ivan Jiang", url = "https://github.com/ivanj04")
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {
}
