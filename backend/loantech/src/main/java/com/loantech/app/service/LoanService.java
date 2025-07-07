package com.loantech.app.service;

import com.loantech.app.dto.PaymentRequest;
import com.loantech.app.entity.Loan;
import com.loantech.app.entity.Payment;
import com.loantech.app.enums.LoanStatus;
import com.loantech.app.enums.PaymentStatus;
import com.loantech.app.repository.LoanRepository;
import com.loantech.app.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Loan> getUserLoans(Long userId) {
        return loanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Prestito non trovato"));
    }

    public List<Payment> getLoanPayments(Long loanId) {
        return paymentRepository.findByLoanIdOrderByDueDateDesc(loanId);
    }

    public Payment makePayment(Long loanId, PaymentRequest request) {
        Loan loan = getLoanById(loanId);

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Il prestito non è attivo");
        }

        if (request.getAmount().compareTo(loan.getRemainingBalance()) > 0) {
            throw new RuntimeException("L'importo supera il debito residuo");
        }

        // Calcola interesse e capitale
        BigDecimal monthlyRate = loan.getInterestRate().divide(new BigDecimal("100")).divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal interestPaid = loan.getRemainingBalance().multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal principalPaid = request.getAmount().subtract(interestPaid);

        // Se il pagamento è minore dell'interesse, tutto va agli interessi
        if (principalPaid.compareTo(BigDecimal.ZERO) < 0) {
            interestPaid = request.getAmount();
            principalPaid = BigDecimal.ZERO;
        }

        Payment payment = new Payment(loan, request.getAmount(), LocalDate.now());
        payment.setPrincipalPaid(principalPaid);
        payment.setInterestPaid(interestPaid);
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.COMPLETED);

        // Aggiorna il saldo del prestito
        loan.makePayment(principalPaid);
        payment.setRemainingBalance(loan.getRemainingBalance());

        loanRepository.save(loan);
        return paymentRepository.save(payment);
    }

    public List<Map<String, Object>> getAmortizationSchedule(Long loanId) {
        Loan loan = getLoanById(loanId);
        List<Map<String, Object>> schedule = new ArrayList<>();

        BigDecimal remainingBalance = loan.getPrincipalAmount();
        BigDecimal monthlyPayment = loan.getMonthlyPayment();
        BigDecimal monthlyRate = loan.getInterestRate().divide(new BigDecimal("100")).divide(new BigDecimal("12"), 6, BigDecimal.ROUND_HALF_UP);
        LocalDate currentDate = loan.getStartDate();

        for (int month = 1; month <= loan.getDurationMonths(); month++) {
            BigDecimal interestPayment = remainingBalance.multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);

            // Assicurati che l'ultimo pagamento copra esattamente il saldo rimanente
            if (month == loan.getDurationMonths()) {
                principalPayment = remainingBalance;
                monthlyPayment = principalPayment.add(interestPayment);
            }

            remainingBalance = remainingBalance.subtract(principalPayment);

            Map<String, Object> payment = new HashMap<>();
            payment.put("month", month);
            payment.put("date", currentDate.plusMonths(month - 1));
            payment.put("monthlyPayment", monthlyPayment);
            payment.put("principalPayment", principalPayment);
            payment.put("interestPayment", interestPayment);
            payment.put("remainingBalance", remainingBalance);

            schedule.add(payment);
        }

        return schedule;
    }
}