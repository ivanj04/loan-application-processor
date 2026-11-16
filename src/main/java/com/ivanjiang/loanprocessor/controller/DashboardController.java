package com.ivanjiang.loanprocessor.controller;

import com.ivanjiang.loanprocessor.dto.DashboardStatsDto;
import com.ivanjiang.loanprocessor.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Aggregate metrics across all loan applications")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final ApplicationService applicationService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics: counts by status, avg risk score, approval rate")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(applicationService.getDashboardStats());
    }
}
