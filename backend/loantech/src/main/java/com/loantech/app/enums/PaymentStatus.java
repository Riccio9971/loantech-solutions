package com.loantech.app.enums;

public enum PaymentStatus {
    PENDING("In Attesa"),
    COMPLETED("Completato"),
    FAILED("Fallito"),
    OVERDUE("In Ritardo");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}