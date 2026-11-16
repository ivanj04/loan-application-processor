package com.ivanjiang.loanprocessor.dto;

import com.ivanjiang.loanprocessor.model.BatchJobRecord;
import com.ivanjiang.loanprocessor.model.JobStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchJobRecordDto {
    private Long id;
    private String batchId;
    private String fileName;
    private int totalRecords;
    private int processedRecords;
    private int approvedCount;
    private int rejectedCount;
    private int reviewCount;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private JobStatus status;

    public static BatchJobRecordDto from(BatchJobRecord record) {
        BatchJobRecordDto dto = new BatchJobRecordDto();
        dto.setId(record.getId());
        dto.setBatchId(record.getBatchId());
        dto.setFileName(record.getFileName());
        dto.setTotalRecords(record.getTotalRecords());
        dto.setProcessedRecords(record.getProcessedRecords());
        dto.setApprovedCount(record.getApprovedCount());
        dto.setRejectedCount(record.getRejectedCount());
        dto.setReviewCount(record.getReviewCount());
        dto.setStartedAt(record.getStartedAt());
        dto.setCompletedAt(record.getCompletedAt());
        dto.setStatus(record.getStatus());
        return dto;
    }
}
