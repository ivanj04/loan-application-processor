package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.LoanApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Step 2: Computes a risk score for each validated loan application.
 *
 * Scoring logic mirrors Freddie Mac LTV/income underwriting thresholds:
 *   Base score:         100
 *   Credit score >750:  +50  (prime borrower bonus)
 *   LTV >80%:           -30  (high loan-to-value penalty)
 *   Income ratio >3:    +20  (income-to-loan ratio bonus)
 */
@Component
@Slf4j
public class LoanScoringProcessor implements ItemProcessor<LoanApplication, LoanApplication> {

    private static final int BASE_SCORE = 100;
    private static final int PRIME_CREDIT_BONUS = 50;
    private static final int HIGH_LTV_PENALTY = 30;
    private static final int GOOD_INCOME_RATIO_BONUS = 20;

    @Override
    public LoanApplication process(LoanApplication application) {
        int score = BASE_SCORE;

        // Credit score bonus: prime borrower (>750 FICO)
        if (application.getCreditScore() > 750) {
            score += PRIME_CREDIT_BONUS;
            log.debug("{}: +{} prime credit bonus (score={})",
                    application.getApplicationId(), PRIME_CREDIT_BONUS, application.getCreditScore());
        }

        // LTV penalty: loan > 80% of property value
        BigDecimal ltv = application.getLoanAmount()
                .divide(application.getPropertyValue(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        if (ltv.compareTo(BigDecimal.valueOf(80)) > 0) {
            score -= HIGH_LTV_PENALTY;
            log.debug("{}: -{} high LTV penalty (LTV={}%)",
                    application.getApplicationId(), HIGH_LTV_PENALTY, ltv.setScale(1, RoundingMode.HALF_UP));
        }

        // Income ratio bonus: annual income > 3x loan amount
        BigDecimal incomeRatio = application.getAnnualIncome()
                .divide(application.getLoanAmount(), 4, RoundingMode.HALF_UP);
        if (incomeRatio.compareTo(BigDecimal.valueOf(3)) > 0) {
            score += GOOD_INCOME_RATIO_BONUS;
            log.debug("{}: +{} income ratio bonus (ratio={})",
                    application.getApplicationId(), GOOD_INCOME_RATIO_BONUS,
                    incomeRatio.setScale(2, RoundingMode.HALF_UP));
        }

        application.setRiskScore(score);
        log.debug("{}: final risk score = {}", application.getApplicationId(), score);
        return application;
    }
}
