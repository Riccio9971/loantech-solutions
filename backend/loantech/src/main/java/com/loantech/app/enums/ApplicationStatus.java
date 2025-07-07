package com.loantech.app.enums;

public enum ApplicationStatus {
    PENDING("In Attesa"),
    UNDER_REVIEW("In Valutazione"),
    APPROVED("Approvato"),
    REJECTED("Respinto"),
    DOCUMENTS_REQUIRED("Documenti Richiesti");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}