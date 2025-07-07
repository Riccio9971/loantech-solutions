package com.loantech.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loantech.app.enums.LoanStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Column(name = "application_id", insertable = false, updatable = false)
    private Long applicationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyPayment;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    // Constructors
    public Loan() {}

    public Loan(LoanApplication application, BigDecimal interestRate, LocalDate startDate) {
        this.application = application;
        this.user = application.getUser();
        this.principalAmount = application.getRequestedAmount();
        this.interestRate = interestRate;
        this.durationMonths = application.getDurationMonths();
        this.monthlyPayment = application.calculateMonthlyPayment(interestRate);
        this.remainingBalance = this.principalAmount;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(this.durationMonths);
    }

    @PostLoad
    private void postLoad() {
        if (application != null) {
            this.applicationId = application.getApplicationId();
        }
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    // Business methods
    public void makePayment(BigDecimal paymentAmount) {
        if (remainingBalance.compareTo(paymentAmount) >= 0) {
            remainingBalance = remainingBalance.subtract(paymentAmount);
            if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
                status = LoanStatus.PAID_OFF;
            }
        }
    }

    // Getters and Setters
    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public LoanApplication getApplication() { return application; }
    public void setApplication(LoanApplication application) {
        this.application = application;
        this.applicationId = application != null ? application.getApplicationId() : null;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getUserId() : null;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public BigDecimal getMonthlyPayment() { return monthlyPayment; }
    public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
}