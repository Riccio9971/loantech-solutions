package com.loantech.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "credit_scores")
@EntityListeners(AuditingEntityListener.class)
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String riskCategory;

    @CreatedDate
    private LocalDateTime calculatedAt;

    // Constructors
    public CreditScore() {}

    public CreditScore(User user, Integer score, String riskCategory) {
        this.user = user;
        this.score = score;
        this.riskCategory = riskCategory;
    }

    @PostLoad
    private void postLoad() {
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    // Business methods
    public String calculateRiskCategory() {
        if (score >= 750) return "OTTIMO";
        if (score >= 650) return "BUONO";
        if (score >= 550) return "DISCRETO";
        if (score >= 450) return "SCARSO";
        return "PESSIMO";
    }

    // Getters and Setters
    public Long getScoreId() { return scoreId; }
    public void setScoreId(Long scoreId) { this.scoreId = scoreId; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getUserId() : null;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) {
        this.score = score;
        this.riskCategory = calculateRiskCategory();
    }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}