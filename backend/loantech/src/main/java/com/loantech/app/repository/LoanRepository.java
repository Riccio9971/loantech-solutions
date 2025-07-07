package com.loantech.app.repository;

import com.loantech.app.entity.Loan;
import com.loantech.app.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserUserId(Long userId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserUserIdAndStatus(Long userId, LoanStatus status);

    @Query("SELECT l FROM Loan l WHERE l.user.userId = :userId ORDER BY l.createdAt DESC")
    List<Loan> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT SUM(l.remainingBalance) FROM Loan l WHERE l.user.userId = :userId AND l.status = :status")
    BigDecimal getTotalRemainingBalanceByUserIdAndStatus(@Param("userId") Long userId, @Param("status") LoanStatus status);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.user.userId = :userId AND l.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") LoanStatus status);
}