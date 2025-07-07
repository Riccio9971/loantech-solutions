package com.loantech.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loantech.app.enums.ApplicationStatus;
import com.loantech.app.enums.LoanType;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_applications")
@EntityListeners(AuditingEntityListener.class)
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Campo per mostrare solo l'ID dell'utente nel JSON
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer durationMonths;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(nullable = false)
    private String employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreatedDate
    private LocalDateTime submittedAt;

    private LocalDateTime processedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Loan loan;

    @JsonIgnore
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();

    // Constructors
    public LoanApplication() {}

    public LoanApplication(User user, LoanType loanType, BigDecimal requestedAmount,
                           Integer durationMonths, BigDecimal monthlyIncome, String employmentStatus) {
        this.user = user;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
        this.durationMonths = durationMonths;
        this.monthlyIncome = monthlyIncome;
        this.employmentStatus = employmentStatus;
    }

    @PostLoad
    private void postLoad() {
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    // Business methods
    public BigDecimal calculateMonthlyPayment(BigDecimal interestRate) {
        if (requestedAmount == null || durationMonths == null || interestRate == null) {
            return BigDecimal.ZERO;
        }

        double monthlyRate = interestRate.doubleValue() / 100 / 12;
        double principal = requestedAmount.doubleValue();
        int months = durationMonths;

        if (monthlyRate == 0) {
            return BigDecimal.valueOf(principal / months);
        }

        double monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, months)) /
                (Math.pow(1 + monthlyRate, months) - 1);

        return BigDecimal.valueOf(monthlyPayment).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getUserId() : null;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
}