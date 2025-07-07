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
}