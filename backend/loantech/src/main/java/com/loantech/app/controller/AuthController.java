package com.loantech.app.controller;

import com.loantech.app.dto.LoginRequest;
import com.loantech.app.dto.RegisterRequest;
import com.loantech.app.dto.ApiResponse;
import com.loantech.app.entity.User;
import com.loantech.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
        origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "false"
)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Tentativo registrazione per email: {}", request.getEmail());
            User user = userService.register(request);
            logger.info("Registrazione completata per: {}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Registrazione completata con successo", user));
        } catch (Exception e) {
            logger.error("Errore durante la registrazione: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Tentativo login per email: {}", request.getEmail());
            User user = userService.login(request);
            logger.info("Login completato per: {}", user.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Login effettuato con successo", user));
        } catch (Exception e) {
            logger.error("Errore durante il login: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse> test() {
        return ResponseEntity.ok(new ApiResponse(true, "API Auth funziona correttamente!", null));
    }

    // ENDPOINT TEMPORANEO PER GENERARE HASH CORRETTO
    @PostMapping("/generate-hash")
    public ResponseEntity<ApiResponse> generateHash(@RequestBody String password) {
        try {
            String hash = passwordEncoder.encode(password);
            logger.info("Hash generato per password '{}': {}", password, hash);

            // Test immediato del hash generato
            boolean testMatch = passwordEncoder.matches(password, hash);
            logger.info("Test match del hash generato: {}", testMatch);

            return ResponseEntity.ok(new ApiResponse(true, "Hash generato: " + hash, hash));
        } catch (Exception e) {
            logger.error("Errore durante la generazione hash: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // ENDPOINT TEMPORANEO PER TESTARE HASH ESISTENTE
    @PostMapping("/test-hash")
    public ResponseEntity<ApiResponse> testHash(@RequestParam String password, @RequestParam String hash) {
        try {
            boolean matches = passwordEncoder.matches(password, hash);
            logger.info("Test hash - Password: '{}', Hash: '{}', Match: {}", password, hash, matches);

            return ResponseEntity.ok(new ApiResponse(true, "Test completato. Match: " + matches, matches));
        } catch (Exception e) {
            logger.error("Errore durante il test hash: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}