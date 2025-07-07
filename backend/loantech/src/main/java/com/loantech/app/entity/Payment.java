package com.loantech.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loantech.app.enums.PaymentStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(name = "loan_id", insertable = false, updatable = false)
    private Long loanId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalPaid;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal interestPaid;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @CreatedDate
    private LocalDateTime createdAt;

    // Constructors
    public Payment() {}

    public Payment(Loan loan, BigDecimal amount, LocalDate dueDate) {
        this.loan = loan;
        this.amount = amount;
        this.dueDate = dueDate;
        this.remainingBalance = loan.getRemainingBalance();
    }

    @PostLoad
    private void postLoad() {
        if (loan != null) {
            this.loanId = loan.getLoanId();
        }
    }

    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) {
        this.loan = loan;
        this.loanId = loan != null ? loan.getLoanId() : null;
    }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPrincipalPaid() { return principalPaid; }
    public void setPrincipalPaid(BigDecimal principalPaid) { this.principalPaid = principalPaid; }

    public BigDecimal getInterestPaid() { return interestPaid; }
    public void setInterestPaid(BigDecimal interestPaid) { this.interestPaid = interestPaid; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
