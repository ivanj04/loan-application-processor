package com.ivanjiang.loanprocessor.batch;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a raw CSV row before validation and enrichment.
 */
@Data
@NoArgsConstructor
public class LoanCsvRecord {
    private String applicationId;
    private String borrowerName;
    private BigDecimal loanAmount;
    private BigDecimal propertyValue;
    private BigDecimal annualIncome;
    private Integer creditScore;
    private String loanType;
}
