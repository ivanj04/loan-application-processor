package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanApplication;
import com.ivanjiang.loanprocessor.model.LoanType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Step 1: Validates CSV records and converts to LoanApplication entities.
 * Invalid records are skipped (return null).
 */
@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class LoanValidationProcessor implements ItemProcessor<LoanCsvRecord, LoanApplication> {

    @Value("#{jobParameters['batchId']}")
    private String batchId;

    @Override
    public LoanApplication process(LoanCsvRecord record) {
        if (!isValid(record)) {
            log.warn("Skipping invalid record: applicationId={}", record.getApplicationId());
            return null;
        }

        LoanType loanType;
        try {
            loanType = LoanType.valueOf(record.getLoanType().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown loan type '{}' for applicationId={}", record.getLoanType(), record.getApplicationId());
            return null;
        }

        return LoanApplication.builder()
                .applicationId(record.getApplicationId())
                .borrowerName(record.getBorrowerName())
                .loanAmount(record.getLoanAmount())
                .propertyValue(record.getPropertyValue())
                .annualIncome(record.getAnnualIncome())
                .creditScore(record.getCreditScore())
                .loanType(loanType)
                .status(ApplicationStatus.PENDING)
                .riskScore(0)
                .processingBatchId(batchId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private boolean isValid(LoanCsvRecord record) {
        if (record.getApplicationId() == null || record.getApplicationId().isBlank()) {
            log.warn("Missing applicationId");
            return false;
        }
        if (record.getBorrowerName() == null || record.getBorrowerName().isBlank()) {
            log.warn("Missing borrowerName for {}", record.getApplicationId());
            return false;
        }
        if (record.getCreditScore() == null || record.getCreditScore() < 300 || record.getCreditScore() > 850) {
            log.warn("Invalid creditScore {} for {}", record.getCreditScore(), record.getApplicationId());
            return false;
        }
        if (record.getLoanAmount() == null || record.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid loanAmount {} for {}", record.getLoanAmount(), record.getApplicationId());
            return false;
        }
        if (record.getPropertyValue() == null || record.getPropertyValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid propertyValue {} for {}", record.getPropertyValue(), record.getApplicationId());
            return false;
        }
        if (record.getAnnualIncome() == null || record.getAnnualIncome().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid annualIncome {} for {}", record.getAnnualIncome(), record.getApplicationId());
            return false;
        }
        if (record.getLoanType() == null || record.getLoanType().isBlank()) {
            log.warn("Missing loanType for {}", record.getApplicationId());
            return false;
        }
        return true;
    }
}
