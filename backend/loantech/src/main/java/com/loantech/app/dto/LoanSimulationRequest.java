package com.loantech.app.dto;

import com.loantech.app.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LoanSimulationRequest {
    @NotNull(message = "Il tipo di prestito è obbligatorio")
    private LoanType loanType;

    @NotNull(message = "L'importo è obbligatorio")
    @DecimalMin(value = "1000.0", message = "L'importo minimo è 1000€")
    private BigDecimal amount;

    @NotNull(message = "La durata è obbligatoria")
    @Min(value = 6, message = "La durata minima è 6 mesi")
    private Integer durationMonths;

    // Getters and Setters
    public LoanType getLoanType() { return loanType; }
    public void setLoanType(LoanType loanType) { this.loanType = loanType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
}