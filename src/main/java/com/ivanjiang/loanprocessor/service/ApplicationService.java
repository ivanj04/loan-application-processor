package com.ivanjiang.loanprocessor.service;

import com.ivanjiang.loanprocessor.dto.DashboardStatsDto;
import com.ivanjiang.loanprocessor.dto.LoanApplicationDto;
import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanType;
import com.ivanjiang.loanprocessor.repository.BatchJobRecordRepository;
import com.ivanjiang.loanprocessor.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final BatchJobRecordRepository batchJobRecordRepository;

    public Page<LoanApplicationDto> getApplications(ApplicationStatus status, LoanType loanType, Pageable pageable) {
        if (status != null && loanType != null) {
            return loanApplicationRepository.findByStatusAndLoanType(status, loanType, pageable)
                    .map(LoanApplicationDto::from);
        } else if (status != null) {
            return loanApplicationRepository.findByStatus(status, pageable)
                    .map(LoanApplicationDto::from);
        } else if (loanType != null) {
            return loanApplicationRepository.findByLoanType(loanType, pageable)
                    .map(LoanApplicationDto::from);
        }
        return loanApplicationRepository.findAll(pageable).map(LoanApplicationDto::from);
    }

    public LoanApplicationDto getApplication(String applicationId) {
        return loanApplicationRepository.findByApplicationId(applicationId)
                .map(LoanApplicationDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
    }

    public DashboardStatsDto getDashboardStats() {
        long total = loanApplicationRepository.countTotal();
        long approved = loanApplicationRepository.countByStatus(ApplicationStatus.APPROVED);
        long rejected = loanApplicationRepository.countByStatus(ApplicationStatus.REJECTED);
        long review = loanApplicationRepository.countByStatus(ApplicationStatus.REVIEW);
        long pending = loanApplicationRepository.countByStatus(ApplicationStatus.PENDING);
        Double avgRisk = loanApplicationRepository.findAverageRiskScore();
        long totalJobs = batchJobRecordRepository.count();

        double approvalRate = total > 0 ? (double) approved / total * 100 : 0;

        return DashboardStatsDto.builder()
                .totalApplications(total)
                .pendingCount(pending)
                .approvedCount(approved)
                .rejectedCount(rejected)
                .reviewCount(review)
                .averageRiskScore(avgRisk != null ? avgRisk : 0.0)
                .approvalRate(approvalRate)
                .totalBatchJobs(totalJobs)
                .build();
    }
}
