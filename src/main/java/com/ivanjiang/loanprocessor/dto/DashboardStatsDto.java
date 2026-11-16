package com.ivanjiang.loanprocessor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDto {
    private long totalApplications;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
    private long reviewCount;
    private double averageRiskScore;
    private double approvalRate;
    private long totalBatchJobs;
}
