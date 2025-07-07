package com.loantech.app.controller;

import com.loantech.app.dto.ApiResponse;
import com.loantech.app.dto.LoanApplicationRequest;
import com.loantech.app.dto.LoanSimulationRequest;
import com.loantech.app.entity.LoanApplication;
import com.loantech.app.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loan-applications")
@CrossOrigin(origins = "http://localhost:3000")
public class LoanApplicationController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponse> createApplication(@Valid @RequestBody LoanApplicationRequest request) {
        try {
            LoanApplication application = loanApplicationService.createApplication(request);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta prestito creata con successo", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserApplications(@PathVariable Long userId) {
        try {
            List<LoanApplication> applications = loanApplicationService.getUserApplications(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Richieste recuperate con successo", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApiResponse> getApplicationById(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta recuperata con successo", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse> simulateLoan(@Valid @RequestBody LoanSimulationRequest request) {
        try {
            var simulation = loanApplicationService.simulateLoan(request);
            return ResponseEntity.ok(new ApiResponse(true, "Simulazione completata", simulation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{applicationId}/approve")
    public ResponseEntity<ApiResponse> approveApplication(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.approveApplication(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta approvata con successo", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{applicationId}/reject")
    public ResponseEntity<ApiResponse> rejectApplication(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.rejectApplication(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta respinta", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}