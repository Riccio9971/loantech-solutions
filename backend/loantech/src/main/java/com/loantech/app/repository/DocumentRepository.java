package com.loantech.app.repository;

import com.loantech.app.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByApplicationApplicationId(Long applicationId);
    List<Document> findByDocumentType(String documentType);
    List<Document> findByApplicationApplicationIdAndDocumentType(Long applicationId, String documentType);
    List<Document> findByApplicationApplicationIdAndVerified(Long applicationId, Boolean verified);
}