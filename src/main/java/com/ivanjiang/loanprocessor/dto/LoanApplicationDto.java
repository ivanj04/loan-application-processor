package com.ivanjiang.loanprocessor.dto;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanApplication;
import com.ivanjiang.loanprocessor.model.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanApplicationDto {
    private Long id;
    private String applicationId;
    private String borrowerName;
    private BigDecimal loanAmount;
    private BigDecimal propertyValue;
    private BigDecimal annualIncome;
    private int creditScore;
    private LoanType loanType;
    private ApplicationStatus status;
    private int riskScore;
    private String processingBatchId;
    private LocalDateTime createdAt;

    public static LoanApplicationDto from(LoanApplication app) {
        LoanApplicationDto dto = new LoanApplicationDto();
        dto.setId(app.getId());
        dto.setApplicationId(app.getApplicationId());
        dto.setBorrowerName(app.getBorrowerName());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setPropertyValue(app.getPropertyValue());
        dto.setAnnualIncome(app.getAnnualIncome());
        dto.setCreditScore(app.getCreditScore());
        dto.setLoanType(app.getLoanType());
        dto.setStatus(app.getStatus());
        dto.setRiskScore(app.getRiskScore());
        dto.setProcessingBatchId(app.getProcessingBatchId());
        dto.setCreatedAt(app.getCreatedAt());
        return dto;
    }
}
