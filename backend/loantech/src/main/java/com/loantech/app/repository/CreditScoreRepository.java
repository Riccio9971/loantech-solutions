package com.loantech.app.repository;

import com.loantech.app.entity.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    Optional<CreditScore> findByUserUserId(Long userId);
    boolean existsByUserUserId(Long userId);
}