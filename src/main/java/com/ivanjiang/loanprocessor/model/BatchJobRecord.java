package com.ivanjiang.loanprocessor.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_job_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchJobRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false, unique = true)
    private String batchId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "total_records")
    @Builder.Default
    private int totalRecords = 0;

    @Column(name = "processed_records")
    @Builder.Default
    private int processedRecords = 0;

    @Column(name = "approved_count")
    @Builder.Default
    private int approvedCount = 0;

    @Column(name = "rejected_count")
    @Builder.Default
    private int rejectedCount = 0;

    @Column(name = "review_count")
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.RUNNING;
}
