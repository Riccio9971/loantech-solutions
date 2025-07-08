package com.loantech.app.repository;

import com.loantech.app.entity.LoanApplication;
import com.loantech.app.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    // ===== METODI BASE (TESTATI E FUNZIONANTI) =====

    /**
     * Trova tutte le richieste di un utente ordinate per data
     */
    List<LoanApplication> findByUserIdOrderBySubmittedAtDesc(Long userId);

    /**
     * Conta le richieste di un utente per status
     */
    Long countByUserIdAndStatus(Long userId, ApplicationStatus status);

    // ===== METODI PER ADMIN DASHBOARD =====

    /**
     * Conta le richieste per status
     */
    Long countByStatus(ApplicationStatus status);

    /**
     * Trova richieste per status con paginazione
     */
    Page<LoanApplication> findByStatus(ApplicationStatus status, Pageable pageable);

    /**
     * Trova richieste per tipo prestito con paginazione
     */
    @Query("SELECT la FROM LoanApplication la WHERE la.loanType = :loanType")
    Page<LoanApplication> findByLoanType(@Param("loanType") String loanType, Pageable pageable);

    /**
     * Statistiche per tipologia di prestito
     */
    @Query("SELECT la.loanType, COUNT(la), AVG(la.requestedAmount), SUM(la.requestedAmount) " +
            "FROM LoanApplication la " +
            "GROUP BY la.loanType " +
            "ORDER BY COUNT(la) DESC")
    List<Object[]> findLoanTypeStatistics();

    /**
     * Statistiche mensili delle richieste degli ultimi 12 mesi
     */
    @Query("SELECT YEAR(la.submittedAt), MONTH(la.submittedAt), COUNT(la), AVG(la.requestedAmount) " +
            "FROM LoanApplication la " +
            "WHERE la.submittedAt >= CURRENT_DATE - 365 " +
            "GROUP BY YEAR(la.submittedAt), MONTH(la.submittedAt) " +
            "ORDER BY YEAR(la.submittedAt) DESC, MONTH(la.submittedAt) DESC")
    List<Object[]> findMonthlyApplicationStats();

    // ===== METODI DI RICERCA AVANZATA =====

    /**
     * Trova richieste per range di importo
     */
    @Query("SELECT la FROM LoanApplication la WHERE la.requestedAmount BETWEEN :minAmount AND :maxAmount")
    Page<LoanApplication> findByAmountRange(@Param("minAmount") BigDecimal minAmount,
                                            @Param("maxAmount") BigDecimal maxAmount,
                                            Pageable pageable);

    /**
     * Trova richieste per range di date
     */
    @Query("SELECT la FROM LoanApplication la WHERE la.submittedAt BETWEEN :startDate AND :endDate")
    Page<LoanApplication> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);

    /**
     * Ricerca avanzata con filtri multipli
     */
    @Query("SELECT la FROM LoanApplication la " +
            "WHERE (:status IS NULL OR la.status = :status) " +
            "AND (:loanType IS NULL OR la.loanType = :loanType) " +
            "AND (:minAmount IS NULL OR la.requestedAmount >= :minAmount) " +
            "AND (:maxAmount IS NULL OR la.requestedAmount <= :maxAmount) " +
            "AND (:startDate IS NULL OR la.submittedAt >= :startDate) " +
            "AND (:endDate IS NULL OR la.submittedAt <= :endDate)")
    Page<LoanApplication> findWithFilters(@Param("status") ApplicationStatus status,
                                          @Param("loanType") String loanType,
                                          @Param("minAmount") BigDecimal minAmount,
                                          @Param("maxAmount") BigDecimal maxAmount,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    // ===== STATISTICHE AGGIUNTIVE (SEMPLIFICATE) =====

    /**
     * Statistiche per employment status
     */
    @Query("SELECT la.employmentStatus, COUNT(la), AVG(la.requestedAmount) " +
            "FROM LoanApplication la " +
            "GROUP BY la.employmentStatus " +
            "ORDER BY COUNT(la) DESC")
    List<Object[]> findEmploymentStatusStatistics();

    /**
     * Richieste che necessitano attenzione (vecchie pending)
     */
    @Query("SELECT la FROM LoanApplication la " +
            "WHERE la.status = 'PENDING' " +
            "AND la.submittedAt < :cutoffDate " +
            "ORDER BY la.submittedAt ASC")
    List<LoanApplication> findOldPendingApplications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Top richieste per importo
     */
    @Query("SELECT la FROM LoanApplication la " +
            "WHERE la.status = :status " +
            "ORDER BY la.requestedAmount DESC")
    Page<LoanApplication> findTopByAmountAndStatus(@Param("status") ApplicationStatus status, Pageable pageable);

    /**
     * Conteggio richieste per mese corrente
     */
    @Query("SELECT COUNT(la) FROM LoanApplication la " +
            "WHERE YEAR(la.submittedAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(la.submittedAt) = MONTH(CURRENT_DATE)")
    Long countCurrentMonthApplications();

    /**
     * Conteggio richieste per anno corrente
     */
    @Query("SELECT COUNT(la) FROM LoanApplication la " +
            "WHERE YEAR(la.submittedAt) = YEAR(CURRENT_DATE)")
    Long countCurrentYearApplications();

    /**
     * Richieste per utente specifico con filtro status
     */
    @Query("SELECT la FROM LoanApplication la " +
            "WHERE la.user.userId = :userId " +
            "AND (:status IS NULL OR la.status = :status) " +
            "ORDER BY la.submittedAt DESC")
    List<LoanApplication> findByUserIdAndStatusOptional(@Param("userId") Long userId,
                                                        @Param("status") ApplicationStatus status);

    /**
     * Somma totale importi richiesti per status
     */
    @Query("SELECT SUM(la.requestedAmount) FROM LoanApplication la WHERE la.status = :status")
    BigDecimal sumRequestedAmountByStatus(@Param("status") ApplicationStatus status);

    // ===== METODI SPRING DATA JPA (SENZA @Query CUSTOM) =====

    /**
     * Trova per status e data (usando Spring Data naming convention)
     */
    List<LoanApplication> findByStatusAndSubmittedAtAfterOrderBySubmittedAtDesc(
            ApplicationStatus status, LocalDateTime submittedAt);

    /**
     * Trova per range di date (usando Spring Data naming convention)
     */
    List<LoanApplication> findBySubmittedAtAfterOrderBySubmittedAtDesc(LocalDateTime submittedAt);

    /**
     * Trova per multipli status
     */
    List<LoanApplication> findByStatusInOrderBySubmittedAtDesc(List<ApplicationStatus> statuses);

    /**
     * Conta per range di date
     */
    Long countBySubmittedAtAfter(LocalDateTime submittedAt);

    /**
     * Trova le prime N richieste ordinate per data
     */
    List<LoanApplication> findTop10ByOrderBySubmittedAtDesc();

    /**
     * Trova per status e tipo prestito
     */
    List<LoanApplication> findByStatusAndLoanTypeOrderBySubmittedAtDesc(
            ApplicationStatus status, String loanType);
}