package com.loantech.app.enums;

public enum LoanStatus {
    ACTIVE("Attivo"),
    PAID_OFF("Saldato"),
    DEFAULTED("Insolvente"),
    SUSPENDED("Sospeso");

    private final String displayName;

    LoanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}