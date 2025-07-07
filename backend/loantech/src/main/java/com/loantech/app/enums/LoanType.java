package com.loantech.app.enums;

public enum LoanType {
    PERSONAL_LOAN("Prestito Personale"),
    MORTGAGE("Mutuo Casa"),
    CAR_LOAN("Prestito Auto"),
    BUSINESS_LOAN("Prestito Business");

    private final String displayName;

    LoanType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}