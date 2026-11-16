package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Step 3: Classifies each application based on its computed risk score.
 *
 *   riskScore >= 150  → APPROVED
 *   riskScore 100–149 → REVIEW
 *   riskScore < 100   → REJECTED
 */
@Component
@Slf4j
public class LoanClassificationProcessor implements ItemProcessor<LoanApplication, LoanApplication> {

    @Override
    public LoanApplication process(LoanApplication application) {
        int score = application.getRiskScore();
        ApplicationStatus status;

        if (score >= 150) {
            status = ApplicationStatus.APPROVED;
        } else if (score >= 100) {
            status = ApplicationStatus.REVIEW;
        } else {
            status = ApplicationStatus.REJECTED;
        }

        application.setStatus(status);
        log.debug("{}: riskScore={} → {}", application.getApplicationId(), score, status);
        return application;
    }
}
