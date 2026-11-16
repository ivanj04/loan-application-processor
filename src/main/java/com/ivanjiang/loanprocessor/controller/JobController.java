package com.ivanjiang.loanprocessor.controller;

import com.ivanjiang.loanprocessor.dto.BatchJobRecordDto;
import com.ivanjiang.loanprocessor.dto.JobTriggerResponse;
import com.ivanjiang.loanprocessor.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Batch Jobs", description = "Trigger and monitor loan processing batch jobs")
@SecurityRequirement(name = "bearerAuth")
public class JobController {

    private final JobService jobService;

    @PostMapping(value = "/trigger", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a CSV file and trigger a loan processing batch job")
    public ResponseEntity<JobTriggerResponse> triggerJob(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.accepted().body(jobService.triggerJob(file));
    }

    @GetMapping
    @Operation(summary = "List all batch job records")
    public ResponseEntity<List<BatchJobRecordDto>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{batchId}")
    @Operation(summary = "Get a specific batch job status and summary")
    public ResponseEntity<BatchJobRecordDto> getJob(@PathVariable String batchId) {
        return ResponseEntity.ok(jobService.getJobByBatchId(batchId));
    }
}
