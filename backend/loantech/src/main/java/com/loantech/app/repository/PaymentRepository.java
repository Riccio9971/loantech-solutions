package com.loantech.app.repository;

import com.loantech.app.entity.Payment;
import com.loantech.app.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByLoanLoanId(Long loanId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByLoanLoanIdAndStatus(Long loanId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.loan.loanId = :loanId ORDER BY p.dueDate DESC")
    List<Payment> findByLoanIdOrderByDueDateDesc(@Param("loanId") Long loanId);

    @Query("SELECT p FROM Payment p WHERE p.dueDate < :date AND p.status = :status")
    List<Payment> findOverduePayments(@Param("date") LocalDate date, @Param("status") PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.loan.user.userId = :userId ORDER BY p.dueDate DESC")
    List<Payment> findByUserIdOrderByDueDateDesc(@Param("userId") Long userId);
}