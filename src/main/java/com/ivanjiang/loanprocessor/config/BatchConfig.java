package com.ivanjiang.loanprocessor.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch 5 configuration.
 *
 * NOTE: @EnableBatchProcessing is intentionally OMITTED for Spring Boot 3.x.
 * In Spring Boot 3 + Spring Batch 5, using @EnableBatchProcessing disables
 * Spring Boot's BatchAutoConfiguration, which means:
 *   - spring.batch.jdbc.initialize-schema=always has no effect (meta-tables not created)
 *   - spring.batch.job.enabled=false has no effect (jobs auto-run on startup)
 *
 * Without @EnableBatchProcessing, Spring Boot auto-configures:
 *   - JobRepository backed by the application DataSource
 *   - JobLauncher with SimpleAsyncTaskExecutor
 *   - Batch schema initialisation via spring.batch.jdbc.initialize-schema
 *   - spring.batch.job.enabled=false to prevent auto-execution on startup
 */
@Configuration
public class BatchConfig {
    // Spring Boot 3 auto-configures everything Batch needs.
    // Additional beans (custom TaskExecutor, secondary DataSource, etc.) go here.
}
