package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanApplication;
import com.ivanjiang.loanprocessor.model.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanScoringProcessorTest {

    private LoanScoringProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new LoanScoringProcessor();
    }

    private LoanApplication buildApp(int creditScore, BigDecimal loan, BigDecimal property, BigDecimal income) {
        return LoanApplication.builder()
                .applicationId("TEST-001")
                .borrowerName("Test User")
                .creditScore(creditScore)
                .loanAmount(loan)
                .propertyValue(property)
                .annualIncome(income)
                .loanType(LoanType.CONVENTIONAL)
                .status(ApplicationStatus.PENDING)
                .build();
    }

    @Test
    void baseScore_noBonus_noPenalty() throws Exception {
        // Credit <= 750, LTV <= 80%, income ratio <= 3
        LoanApplication app = buildApp(700,
                new BigDecimal("160000"), new BigDecimal("200000"), new BigDecimal("400000"));
        LoanApplication result = processor.process(app);
        assertThat(result.getRiskScore()).isEqualTo(100);
    }

    @Test
    void primeCreditBonus() throws Exception {
        LoanApplication app = buildApp(760,
                new BigDecimal("160000"), new BigDecimal("200000"), new BigDecimal("400000"));
        LoanApplication result = processor.process(app);
        assertThat(result.getRiskScore()).isEqualTo(150); // 100 + 50
    }

    @Test
    void highLtvPenalty() throws Exception {
        // LTV = 90% (180k / 200k)
        LoanApplication app = buildApp(700,
                new BigDecimal("180000"), new BigDecimal("200000"), new BigDecimal("400000"));
        LoanApplication result = processor.process(app);
        assertThat(result.getRiskScore()).isEqualTo(70); // 100 - 30
    }

    @Test
    void goodIncomeRatioBonus() throws Exception {
        // income ratio = 4 (400k / 100k)
        LoanApplication app = buildApp(700,
                new BigDecimal("100000"), new BigDecimal("200000"), new BigDecimal("400000"));
        LoanApplication result = processor.process(app);
        assertThat(result.getRiskScore()).isEqualTo(120); // 100 + 20
    }

    @Test
    void allBonuses_approved() throws Exception {
        // prime credit, good LTV, good income ratio
        LoanApplication app = buildApp(780,
                new BigDecimal("160000"), new BigDecimal("200000"), new BigDecimal("600000"));
        LoanApplication result = processor.process(app);
        // 100 + 50 (credit) + 20 (income ratio) = 170
        assertThat(result.getRiskScore()).isEqualTo(170);
    }

    @Test
    void classificationApproved() throws Exception {
        LoanClassificationProcessor classifier = new LoanClassificationProcessor();
        LoanApplication app = buildApp(780,
                new BigDecimal("160000"), new BigDecimal("200000"), new BigDecimal("600000"));
        app.setRiskScore(170);
        LoanApplication result = classifier.process(app);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
    }

    @Test
    void classificationReview() throws Exception {
        LoanClassificationProcessor classifier = new LoanClassificationProcessor();
        LoanApplication app = buildApp(700,
                new BigDecimal("160000"), new BigDecimal("200000"), new BigDecimal("400000"));
        app.setRiskScore(120);
        LoanApplication result = classifier.process(app);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REVIEW);
    }

    @Test
    void classificationRejected() throws Exception {
        LoanClassificationProcessor classifier = new LoanClassificationProcessor();
        LoanApplication app = buildApp(700,
                new BigDecimal("180000"), new BigDecimal("200000"), new BigDecimal("400000"));
        app.setRiskScore(70);
        LoanApplication result = classifier.process(app);
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
    }
}
