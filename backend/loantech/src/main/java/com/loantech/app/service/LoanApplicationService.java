package com.loantech.app.service;

import com.loantech.app.dto.LoanApplicationRequest;
import com.loantech.app.dto.LoanSimulationRequest;
import com.loantech.app.entity.Loan;
import com.loantech.app.entity.LoanApplication;
import com.loantech.app.entity.User;
import com.loantech.app.enums.ApplicationStatus;
import com.loantech.app.enums.LoanType;
import com.loantech.app.repository.LoanApplicationRepository;
import com.loantech.app.repository.LoanRepository;
import com.loantech.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoanApplicationService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    public LoanApplication createApplication(LoanApplicationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Validazioni business
        if (request.getRequestedAmount().compareTo(new BigDecimal("100000")) > 0) {
            throw new RuntimeException("Importo massimo consentito: 100.000€");
        }

        if (request.getDurationMonths() > 120) {
            throw new RuntimeException("Durata massima consentita: 120 mesi");
        }

        LoanApplication application = new LoanApplication(
                user,
                request.getLoanType(),
                request.getRequestedAmount(),
                request.getDurationMonths(),
                request.getMonthlyIncome(),
                request.getEmploymentStatus()
        );

        return loanApplicationRepository.save(application);
    }

    public List<LoanApplication> getUserApplications(Long userId) {
        return loanApplicationRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    public LoanApplication getApplicationById(Long applicationId) {
        return loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Richiesta non trovata"));
    }

    public Map<String, Object> simulateLoan(LoanSimulationRequest request) {
        BigDecimal interestRate = getInterestRateByLoanType(request.getLoanType());

        // Calcolo rata mensile
        double monthlyRate = interestRate.doubleValue() / 100 / 12;
        double principal = request.getAmount().doubleValue();
        int months = request.getDurationMonths();

        double monthlyPayment;
        if (monthlyRate == 0) {
            monthlyPayment = principal / months;
        } else {
            monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
                    (Math.pow(1 + monthlyRate, months) - 1);
        }

        BigDecimal totalAmount = BigDecimal.valueOf(monthlyPayment * months);
        BigDecimal totalInterest = totalAmount.subtract(request.getAmount());

        Map<String, Object> simulation = new HashMap<>();
        simulation.put("requestedAmount", request.getAmount());
        simulation.put("durationMonths", request.getDurationMonths());
        simulation.put("interestRate", interestRate);
        simulation.put("monthlyPayment", BigDecimal.valueOf(monthlyPayment).setScale(2, BigDecimal.ROUND_HALF_UP));
        simulation.put("totalAmount", totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        simulation.put("totalInterest", totalInterest.setScale(2, BigDecimal.ROUND_HALF_UP));

        return simulation;
    }

    public LoanApplication approveApplication(Long applicationId) {
        LoanApplication application = getApplicationById(applicationId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("La richiesta non può essere approvata");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setProcessedAt(LocalDateTime.now());

        // Crea il prestito
        BigDecimal interestRate = getInterestRateByLoanType(application.getLoanType());
        Loan loan = new Loan(application, interestRate, LocalDate.now());
        loanRepository.save(loan);

        return loanApplicationRepository.save(application);
    }

    public LoanApplication rejectApplication(Long applicationId) {
        LoanApplication application = getApplicationById(applicationId);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("La richiesta non può essere respinta");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setProcessedAt(LocalDateTime.now());

        return loanApplicationRepository.save(application);
    }

    private BigDecimal getInterestRateByLoanType(LoanType loanType) {
        switch (loanType) {
            case PERSONAL_LOAN:
                return new BigDecimal("8.5");
            case MORTGAGE:
                return new BigDecimal("3.2");
            case CAR_LOAN:
                return new BigDecimal("6.0");
            case BUSINESS_LOAN:
                return new BigDecimal("7.5");
            default:
                return new BigDecimal("8.0");
        }
    }
}