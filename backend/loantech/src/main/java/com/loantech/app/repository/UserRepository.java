package com.loantech.app.repository;

import com.loantech.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByFiscalCode(String fiscalCode);
    boolean existsByEmail(String email);
    boolean existsByFiscalCode(String fiscalCode);
}