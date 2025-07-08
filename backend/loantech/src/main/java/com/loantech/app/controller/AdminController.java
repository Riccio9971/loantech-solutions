package com.loantech.app.controller;

import com.loantech.app.dto.ApiResponse;
import com.loantech.app.entity.LoanApplication;
import com.loantech.app.enums.ApplicationStatus;
import com.loantech.app.service.LoanApplicationService;
import com.loantech.app.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    /**
     * Dashboard admin con statistiche generali
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getAdminDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Conteggi per status
            Long pendingCount = loanApplicationRepository.countByStatus(ApplicationStatus.PENDING);
            Long underReviewCount = loanApplicationRepository.countByStatus(ApplicationStatus.UNDER_REVIEW);
            Long approvedCount = loanApplicationRepository.countByStatus(ApplicationStatus.APPROVED);
            Long rejectedCount = loanApplicationRepository.countByStatus(ApplicationStatus.REJECTED);

            dashboard.put("pendingApplications", pendingCount);
            dashboard.put("underReviewApplications", underReviewCount);
            dashboard.put("approvedApplications", approvedCount);
            dashboard.put("rejectedApplications", rejectedCount);
            dashboard.put("totalApplications", pendingCount + underReviewCount + approvedCount + rejectedCount);

            // Richieste recenti (ultime 10) - usando metodo Spring Data semplice
            List<LoanApplication> recentApplications = loanApplicationRepository.findTop10ByOrderBySubmittedAtDesc();
            dashboard.put("recentApplications", recentApplications);

            // Statistiche questo mese
            Long thisMonthCount = loanApplicationRepository.countCurrentMonthApplications();
            dashboard.put("thisMonthApplications", thisMonthCount);

            // Statistiche questo anno
            Long thisYearCount = loanApplicationRepository.countCurrentYearApplications();
            dashboard.put("thisYearApplications", thisYearCount);

            return ResponseEntity.ok(new ApiResponse(true, "Dashboard caricata con successo", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento dashboard: " + e.getMessage(), null));
        }
    }

    /**
     * Lista tutte le richieste con filtri opzionali
     */
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String loanType) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<LoanApplication> applications;

            // Applica filtri se specificati
            if (status != null && !status.isEmpty()) {
                ApplicationStatus statusEnum = ApplicationStatus.valueOf(status.toUpperCase());
                applications = loanApplicationRepository.findByStatus(statusEnum, pageable);
            } else {
                applications = loanApplicationRepository.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("applications", applications.getContent());
            response.put("totalElements", applications.getTotalElements());
            response.put("totalPages", applications.getTotalPages());
            response.put("currentPage", applications.getNumber());
            response.put("pageSize", applications.getSize());

            return ResponseEntity.ok(new ApiResponse(true, "Richieste caricate con successo", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento richieste: " + e.getMessage(), null));
        }
    }

    /**
     * Dettagli di una specifica richiesta
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse> getApplicationDetails(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Dettagli richiesta caricati", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento dettagli: " + e.getMessage(), null));
        }
    }

    /**
     * Approva una richiesta prestito
     */
    @PutMapping("/applications/{applicationId}/approve")
    public ResponseEntity<ApiResponse> approveApplication(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.approveApplication(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta approvata con successo", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nell'approvazione: " + e.getMessage(), null));
        }
    }

    /**
     * Respinge una richiesta prestito
     */
    @PutMapping("/applications/{applicationId}/reject")
    public ResponseEntity<ApiResponse> rejectApplication(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.rejectApplication(applicationId);
            return ResponseEntity.ok(new ApiResponse(true, "Richiesta respinta", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel respingimento: " + e.getMessage(), null));
        }
    }

    /**
     * Cambia status a "In Revisione"
     */
    @PutMapping("/applications/{applicationId}/review")
    public ResponseEntity<ApiResponse> setUnderReview(@PathVariable Long applicationId) {
        try {
            LoanApplication application = loanApplicationService.getApplicationById(applicationId);

            if (application.getStatus() != ApplicationStatus.PENDING) {
                throw new RuntimeException("Solo le richieste pendenti possono essere messe in revisione");
            }

            application.setStatus(ApplicationStatus.UNDER_REVIEW);
            LoanApplication updated = loanApplicationRepository.save(application);

            return ResponseEntity.ok(new ApiResponse(true, "Richiesta messa in revisione", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nell'aggiornamento: " + e.getMessage(), null));
        }
    }

    /**
     * Statistiche per tipologia di prestito
     */
    @GetMapping("/statistics/loan-types")
    public ResponseEntity<ApiResponse> getLoanTypeStatistics() {
        try {
            List<Object[]> stats = loanApplicationRepository.findLoanTypeStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("loanTypeStats", stats);

            return ResponseEntity.ok(new ApiResponse(true, "Statistiche per tipologia caricate", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento statistiche: " + e.getMessage(), null));
        }
    }

    /**
     * Statistiche mensili delle richieste
     */
    @GetMapping("/statistics/monthly")
    public ResponseEntity<ApiResponse> getMonthlyStatistics() {
        try {
            List<Object[]> stats = loanApplicationRepository.findMonthlyApplicationStats();

            Map<String, Object> response = new HashMap<>();
            response.put("monthlyStats", stats);

            return ResponseEntity.ok(new ApiResponse(true, "Statistiche mensili caricate", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento statistiche mensili: " + e.getMessage(), null));
        }
    }

    /**
     * Statistiche per employment status
     */
    @GetMapping("/statistics/employment")
    public ResponseEntity<ApiResponse> getEmploymentStatistics() {
        try {
            List<Object[]> stats = loanApplicationRepository.findEmploymentStatusStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("employmentStats", stats);

            return ResponseEntity.ok(new ApiResponse(true, "Statistiche employment caricate", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento statistiche employment: " + e.getMessage(), null));
        }
    }

    /**
     * Richieste che necessitano attenzione (più di 7 giorni in pending)
     */
    @GetMapping("/applications/needs-attention")
    public ResponseEntity<ApiResponse> getApplicationsNeedingAttention() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
            List<LoanApplication> oldApplications = loanApplicationRepository.findOldPendingApplications(cutoffDate);

            Map<String, Object> response = new HashMap<>();
            response.put("applications", oldApplications);
            response.put("count", oldApplications.size());

            return ResponseEntity.ok(new ApiResponse(true, "Richieste che necessitano attenzione caricate", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento: " + e.getMessage(), null));
        }
    }

    /**
     * Richieste recenti (ultime 24 ore)
     */
    @GetMapping("/applications/recent")
    public ResponseEntity<ApiResponse> getRecentApplications() {
        try {
            LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
            List<LoanApplication> recentApplications = loanApplicationRepository.findBySubmittedAtAfterOrderBySubmittedAtDesc(yesterday);

            Map<String, Object> response = new HashMap<>();
            response.put("applications", recentApplications);
            response.put("count", recentApplications.size());

            return ResponseEntity.ok(new ApiResponse(true, "Richieste recenti caricate", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento richieste recenti: " + e.getMessage(), null));
        }
    }

    /**
     * Cambio status multiplo (per operazioni batch)
     */
    @PutMapping("/applications/batch-update")
    public ResponseEntity<ApiResponse> batchUpdateStatus(
            @RequestParam List<Long> applicationIds,
            @RequestParam String newStatus) {

        try {
            ApplicationStatus status = ApplicationStatus.valueOf(newStatus.toUpperCase());
            int updatedCount = 0;

            for (Long id : applicationIds) {
                try {
                    LoanApplication app = loanApplicationService.getApplicationById(id);

                    // Logica di business per transizioni di stato valide
                    if (status == ApplicationStatus.APPROVED && app.getStatus() == ApplicationStatus.PENDING) {
                        loanApplicationService.approveApplication(id);
                        updatedCount++;
                    } else if (status == ApplicationStatus.REJECTED && app.getStatus() == ApplicationStatus.PENDING) {
                        loanApplicationService.rejectApplication(id);
                        updatedCount++;
                    } else if (status == ApplicationStatus.UNDER_REVIEW && app.getStatus() == ApplicationStatus.PENDING) {
                        app.setStatus(ApplicationStatus.UNDER_REVIEW);
                        loanApplicationRepository.save(app);
                        updatedCount++;
                    }
                } catch (Exception e) {
                    // Continua con le altre richieste se una fallisce
                    System.err.println("Errore nell'aggiornamento della richiesta " + id + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("updatedCount", updatedCount);
            response.put("totalRequested", applicationIds.size());

            return ResponseEntity.ok(new ApiResponse(true,
                    "Aggiornamento batch completato: " + updatedCount + "/" + applicationIds.size() + " richieste aggiornate",
                    response));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nell'aggiornamento batch: " + e.getMessage(), null));
        }
    }

    /**
     * Overview generale per admin
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse> getAdminOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();

            // Conteggi base
            overview.put("totalApplications", loanApplicationRepository.count());
            overview.put("pendingCount", loanApplicationRepository.countByStatus(ApplicationStatus.PENDING));
            overview.put("approvedCount", loanApplicationRepository.countByStatus(ApplicationStatus.APPROVED));
            overview.put("rejectedCount", loanApplicationRepository.countByStatus(ApplicationStatus.REJECTED));

            // Conteggi temporali
            LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
            Long recentCount = loanApplicationRepository.countBySubmittedAtAfter(lastWeek);
            overview.put("lastWeekCount", recentCount);

            overview.put("monthlyCount", loanApplicationRepository.countCurrentMonthApplications());
            overview.put("yearlyCount", loanApplicationRepository.countCurrentYearApplications());

            return ResponseEntity.ok(new ApiResponse(true, "Overview admin caricata", overview));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Errore nel caricamento overview: " + e.getMessage(), null));
        }
    }
}