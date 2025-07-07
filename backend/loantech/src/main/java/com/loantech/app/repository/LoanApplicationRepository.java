package com.loantech.app.repository;

import com.loantech.app.entity.LoanApplication;
import com.loantech.app.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUserUserId(Long userId);
    List<LoanApplication> findByStatus(ApplicationStatus status);
    List<LoanApplication> findByUserUserIdAndStatus(Long userId, ApplicationStatus status);

    @Query("SELECT la FROM LoanApplication la WHERE la.user.userId = :userId ORDER BY la.submittedAt DESC")
    List<LoanApplication> findByUserIdOrderBySubmittedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(la) FROM LoanApplication la WHERE la.user.userId = :userId AND la.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ApplicationStatus status);
}