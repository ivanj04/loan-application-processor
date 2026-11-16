package com.ivanjiang.loanprocessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobTriggerResponse {
    private String batchId;
    private String fileName;
    private String message;
    private String status;
}
