package com.loantech.app.controller;

import com.loantech.app.dto.ApiResponse;
import com.loantech.app.dto.PaymentRequest;
import com.loantech.app.entity.Loan;
import com.loantech.app.entity.Payment;
import com.loantech.app.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:3000")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserLoans(@PathVariable Long userId) {
        try {
            List<Loan> loans = loanService.getUserLoans(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Prestiti recuperati con successo", loans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<ApiResponse> getLoanById(@PathVariable Long loanId) {
        try {
            Loan loan = loanService.getLoanById(loanId);
            return ResponseEntity.ok(new ApiResponse(true, "Prestito recuperato con successo", loan));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{loanId}/payments")
    public ResponseEntity<ApiResponse> getLoanPayments(@PathVariable Long loanId) {
        try {
            List<Payment> payments = loanService.getLoanPayments(loanId);
            return ResponseEntity.ok(new ApiResponse(true, "Pagamenti recuperati con successo", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/{loanId}/payments")
    public ResponseEntity<ApiResponse> makePayment(@PathVariable Long loanId, @Valid @RequestBody PaymentRequest request) {
        try {
            Payment payment = loanService.makePayment(loanId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Pagamento effettuato con successo", payment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{loanId}/amortization")
    public ResponseEntity<ApiResponse> getAmortizationSchedule(@PathVariable Long loanId) {
        try {
            var schedule = loanService.getAmortizationSchedule(loanId);
            return ResponseEntity.ok(new ApiResponse(true, "Piano di ammortamento generato", schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}