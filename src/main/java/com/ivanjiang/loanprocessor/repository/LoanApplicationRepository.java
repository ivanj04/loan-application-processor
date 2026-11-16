package com.ivanjiang.loanprocessor.repository;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanApplication;
import com.ivanjiang.loanprocessor.model.LoanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    Optional<LoanApplication> findByApplicationId(String applicationId);

    Page<LoanApplication> findByStatus(ApplicationStatus status, Pageable pageable);

    Page<LoanApplication> findByLoanType(LoanType loanType, Pageable pageable);

    Page<LoanApplication> findByStatusAndLoanType(ApplicationStatus status, LoanType loanType, Pageable pageable);

    List<LoanApplication> findByProcessingBatchId(String batchId);

    long countByStatus(ApplicationStatus status);

    @Query("SELECT AVG(a.riskScore) FROM LoanApplication a")
    Double findAverageRiskScore();

    @Query("SELECT COUNT(a) FROM LoanApplication a WHERE a.status = 'APPROVED'")
    long countApproved();

    @Query("SELECT COUNT(a) FROM LoanApplication a")
    long countTotal();
}
