package com.loantech.app.dto;

import com.loantech.app.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanApplicationRequest {
    @NotNull(message = "L'ID utente è obbligatorio")
    private Long userId;

    @NotNull(message = "Il tipo di prestito è obbligatorio")
    private LoanType loanType;

    @NotNull(message = "L'importo richiesto è obbligatorio")
    @DecimalMin(value = "1000.0", message = "L'importo minimo è 1000€")
    private BigDecimal requestedAmount;

    @NotNull(message = "La durata è obbligatoria")
    @Min(value = 6, message = "La durata minima è 6 mesi")
    private Integer durationMonths;

    @NotNull(message = "Il reddito mensile è obbligatorio")
    @DecimalMin(value = "500.0", message = "Il reddito minimo è 500€")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Lo stato lavorativo è obbligatorio")
    private String employmentStatus;

    // Getters and Setters
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
}