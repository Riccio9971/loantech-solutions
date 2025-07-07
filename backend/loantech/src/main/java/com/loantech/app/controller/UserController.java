package com.loantech.app.controller;

import com.loantech.app.dto.ApiResponse;
import com.loantech.app.entity.User;
import com.loantech.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
        origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = "*",
        allowCredentials = "false"
)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Utente recuperato con successo", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<ApiResponse> getUserDashboard(@PathVariable Long userId) {
        try {
            var dashboard = userService.getUserDashboard(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Dashboard recuperata con successo", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}/credit-score")
    public ResponseEntity<ApiResponse> getUserCreditScore(@PathVariable Long userId) {
        try {
            var creditScore = userService.getUserCreditScore(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Credit score recuperato con successo", creditScore));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}