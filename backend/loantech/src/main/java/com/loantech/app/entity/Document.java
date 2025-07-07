package com.loantech.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@EntityListeners(AuditingEntityListener.class)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private LoanApplication application;

    @Column(name = "application_id", insertable = false, updatable = false)
    private Long applicationId;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Boolean verified = false;

    @CreatedDate
    private LocalDateTime uploadedAt;

    // Constructors
    public Document() {}

    public Document(LoanApplication application, String documentType, String fileName, String filePath) {
        this.application = application;
        this.documentType = documentType;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    @PostLoad
    private void postLoad() {
        if (application != null) {
            this.applicationId = application.getApplicationId();
        }
    }

    // Getters and Setters
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public LoanApplication getApplication() { return application; }
    public void setApplication(LoanApplication application) {
        this.application = application;
        this.applicationId = application != null ? application.getApplicationId() : null;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
