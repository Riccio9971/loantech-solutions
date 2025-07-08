package com.loantech.app.service;

import com.loantech.app.dto.LoginRequest;
import com.loantech.app.dto.RegisterRequest;
import com.loantech.app.entity.CreditScore;
import com.loantech.app.entity.User;
import com.loantech.app.repository.CreditScoreRepository;
import com.loantech.app.repository.LoanApplicationRepository;
import com.loantech.app.repository.LoanRepository;
import com.loantech.app.repository.UserRepository;
import com.loantech.app.enums.ApplicationStatus;
import com.loantech.app.enums.LoanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già registrata");
        }

        if (userRepository.existsByFiscalCode(request.getFiscalCode())) {
            throw new RuntimeException("Codice fiscale già registrato");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setFiscalCode(request.getFiscalCode());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);

        // Genera credit score iniziale
        generateInitialCreditScore(savedUser);

        return savedUser;
    }

    public User login(LoginRequest request) {
        logger.debug("=== INIZIO LOGIN DEBUG ===");
        logger.debug("Email ricevuta: '{}'", request.getEmail());
        logger.debug("Password ricevuta: '{}'", request.getPassword());

        // Verifica se l'utente esiste
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("Utente non trovato per email: {}", request.getEmail());
                    return new RuntimeException("Credenziali non valide");
                });

        logger.debug("Utente trovato: ID={}, Email={}", user.getUserId(), user.getEmail());
        logger.debug("Hash password dal database: '{}'", user.getPasswordHash());

        // Test della validazione password
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        logger.debug("Risultato passwordEncoder.matches(): {}", passwordMatches);

        // Debug aggiuntivo del PasswordEncoder
        logger.debug("Tipo PasswordEncoder: {}", passwordEncoder.getClass().getSimpleName());

        // Test con una password di esempio per verificare l'encoder
        String testHash = passwordEncoder.encode("password123");
        logger.debug("Test hash generato per 'password123': '{}'", testHash);
        boolean testMatch = passwordEncoder.matches("password123", testHash);
        logger.debug("Test match del nuovo hash: {}", testMatch);

        if (!passwordMatches) {
            logger.error("Password non corrisponde per utente: {}", user.getEmail());
            logger.error("Password inserita: '{}'", request.getPassword());
            logger.error("Hash dal database: '{}'", user.getPasswordHash());

            // Test aggiuntivo: verifica se l'hash del database funziona con password123
            boolean directTest = passwordEncoder.matches("password123", user.getPasswordHash());
            logger.error("Test diretto con 'password123': {}", directTest);

            throw new RuntimeException("Credenziali non valide");
        }

        logger.debug("Login completato con successo per utente: {}", user.getEmail());
        logger.debug("=== FINE LOGIN DEBUG ===");

        return user;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public Map<String, Object> getUserDashboard(Long userId) {
        User user = getUserById(userId);
        Map<String, Object> dashboard = new HashMap<>();

        // Statistiche generali
        Long pendingApplications = loanApplicationRepository.countByUserIdAndStatus(userId, ApplicationStatus.PENDING);
        Long activeLoans = loanRepository.countByUserIdAndStatus(userId, LoanStatus.ACTIVE);
        BigDecimal totalDebt = loanRepository.getTotalRemainingBalanceByUserIdAndStatus(userId, LoanStatus.ACTIVE);

        dashboard.put("user", user);
        dashboard.put("pendingApplications", pendingApplications);
        dashboard.put("activeLoans", activeLoans);
        dashboard.put("totalDebt", totalDebt != null ? totalDebt : BigDecimal.ZERO);

        // Credit score
        CreditScore creditScore = creditScoreRepository.findByUserUserId(userId).orElse(null);
        dashboard.put("creditScore", creditScore);

        return dashboard;
    }

    public CreditScore getUserCreditScore(Long userId) {
        return creditScoreRepository.findByUserUserId(userId)
                .orElseThrow(() -> new RuntimeException("Credit score non trovato"));
    }

    private void generateInitialCreditScore(User user) {
        Random random = new Random();
        int score = 500 + random.nextInt(300); // Score tra 500 e 800

        CreditScore creditScore = new CreditScore(user, score, "");
        creditScore.setScore(score); // Questo imposterà automaticamente la categoria

        creditScoreRepository.save(creditScore);
    }

    // Metodo di utility per debug password (da rimuovere in produzione)
    public void debugPasswordTest(String plainPassword, String hashedPassword) {
        logger.debug("=== DEBUG PASSWORD TEST ===");
        logger.debug("Plain password: '{}'", plainPassword);
        logger.debug("Hashed password: '{}'", hashedPassword);
        logger.debug("Match result: {}", passwordEncoder.matches(plainPassword, hashedPassword));
        logger.debug("=== END DEBUG PASSWORD TEST ===");
    }
}