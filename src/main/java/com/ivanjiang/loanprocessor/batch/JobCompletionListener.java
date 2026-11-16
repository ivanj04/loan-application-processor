package com.ivanjiang.loanprocessor.batch;

import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.BatchJobRecord;
import com.ivanjiang.loanprocessor.model.JobStatus;
import com.ivanjiang.loanprocessor.repository.BatchJobRecordRepository;
import com.ivanjiang.loanprocessor.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Updates BatchJobRecord on job start and completion.
 * Tallies approved/rejected/review counts after the job finishes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JobCompletionListener implements JobExecutionListener {

    private final BatchJobRecordRepository batchJobRecordRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        String batchId = jobExecution.getJobParameters().getString("batchId");
        log.info("Starting batch job: batchId={}", batchId);

        batchJobRecordRepository.findByBatchId(batchId).ifPresent(record -> {
            record.setStartedAt(LocalDateTime.now());
            record.setStatus(JobStatus.RUNNING);
            batchJobRecordRepository.save(record);
        });
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String batchId = jobExecution.getJobParameters().getString("batchId");
        log.info("Completed batch job: batchId={}, status={}", batchId, jobExecution.getStatus());

        batchJobRecordRepository.findByBatchId(batchId).ifPresent(record -> {
            record.setCompletedAt(LocalDateTime.now());

            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                record.setStatus(JobStatus.COMPLETED);
                updateCounts(record, batchId);
            } else {
                record.setStatus(JobStatus.FAILED);
            }

            batchJobRecordRepository.save(record);
        });
    }

    private void updateCounts(BatchJobRecord record, String batchId) {
        var applications = loanApplicationRepository.findByProcessingBatchId(batchId);
        int approved = 0, rejected = 0, review = 0;

        for (var app : applications) {
            if (app.getStatus() == ApplicationStatus.APPROVED) approved++;
            else if (app.getStatus() == ApplicationStatus.REJECTED) rejected++;
            else if (app.getStatus() == ApplicationStatus.REVIEW) review++;
        }

        record.setProcessedRecords(applications.size());
        record.setApprovedCount(approved);
        record.setRejectedCount(rejected);
        record.setReviewCount(review);

        log.info("Batch {} results: total={}, approved={}, rejected={}, review={}",
                batchId, applications.size(), approved, rejected, review);
    }
}
