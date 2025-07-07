package com.loantech.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentRequest {
    @NotNull(message = "L'importo è obbligatorio")
    @DecimalMin(value = "0.01", message = "L'importo deve essere maggiore di 0")
    private BigDecimal amount;

    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}