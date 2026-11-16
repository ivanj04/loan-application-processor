package com.ivanjiang.loanprocessor.controller;

import com.ivanjiang.loanprocessor.dto.LoanApplicationDto;
import com.ivanjiang.loanprocessor.model.ApplicationStatus;
import com.ivanjiang.loanprocessor.model.LoanType;
import com.ivanjiang.loanprocessor.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Query processed loan applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "List loan applications with optional status and loanType filters (paginated)")
    public ResponseEntity<Page<LoanApplicationDto>> getApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) LoanType loanType,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplications(status, loanType, pageable));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Get a single loan application by applicationId")
    public ResponseEntity<LoanApplicationDto> getApplication(@PathVariable String applicationId) {
        return ResponseEntity.ok(applicationService.getApplication(applicationId));
    }
}
