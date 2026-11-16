package com.ivanjiang.loanprocessor.service;

import com.ivanjiang.loanprocessor.batch.LoanItemReader;
import com.ivanjiang.loanprocessor.dto.BatchJobRecordDto;
import com.ivanjiang.loanprocessor.dto.JobTriggerResponse;
import com.ivanjiang.loanprocessor.model.BatchJobRecord;
import com.ivanjiang.loanprocessor.model.JobStatus;
import com.ivanjiang.loanprocessor.repository.BatchJobRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobLauncher jobLauncher;
    private final Job loanProcessingJob;
    private final BatchJobRecordRepository batchJobRecordRepository;
    private final LoanItemReader loanItemReader;

    @Transactional
    public JobTriggerResponse triggerJob(MultipartFile file) throws Exception {
        String batchId = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.csv";

        // Save uploaded file to temp location
        Path tempFile = Files.createTempFile("loan-batch-", ".csv");
        file.transferTo(tempFile.toFile());

        // Create tracking record
        BatchJobRecord record = BatchJobRecord.builder()
                .batchId(batchId)
                .fileName(fileName)
                .totalRecords(countLines(tempFile.toFile()))
                .startedAt(LocalDateTime.now())
                .status(JobStatus.RUNNING)
                .build();
        batchJobRecordRepository.save(record);

        // Configure reader resource for this job run
        loanItemReader.setResource(new FileSystemResource(tempFile));

        // Build job parameters
        JobParameters params = new JobParametersBuilder()
                .addString("batchId", batchId)
                .addString("fileName", fileName)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // Launch async
        jobLauncher.run(loanProcessingJob, params);

        log.info("Triggered loanProcessingJob: batchId={}, file={}", batchId, fileName);
        return new JobTriggerResponse(batchId, fileName, "Batch job submitted successfully", "RUNNING");
    }

    public List<BatchJobRecordDto> getAllJobs() {
        return batchJobRecordRepository.findAll().stream()
                .map(BatchJobRecordDto::from)
                .collect(Collectors.toList());
    }

    public BatchJobRecordDto getJobByBatchId(String batchId) {
        return batchJobRecordRepository.findByBatchId(batchId)
                .map(BatchJobRecordDto::from)
                .orElseThrow(() -> new IllegalArgumentException("Batch job not found: " + batchId));
    }

    private int countLines(File file) {
        try (var lines = Files.lines(file.toPath())) {
            // subtract 1 for header
            return (int) Math.max(0, lines.count() - 1);
        } catch (IOException e) {
            return 0;
        }
    }
}
