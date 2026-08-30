package com.expensemanager.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "chit_income")
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chit_id", nullable = false)
    private Chit chit;
    @Column(name = "income_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal incomeAmount;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;
    @Column(name = "number_of_months", nullable = false)
    private Integer numberOfMonths;
    @Column(name = "interest_earned_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal interestEarnedAmount;
    @Column(nullable = false)
    private boolean active;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Income() {
    }

    public Income(Chit chit, BigDecimal incomeAmount, BigDecimal percentage, Integer numberOfMonths,
            BigDecimal interestEarnedAmount) {
        this.chit = chit;
        this.incomeAmount = incomeAmount;
        this.percentage = percentage;
        this.numberOfMonths = numberOfMonths;
        this.interestEarnedAmount = interestEarnedAmount;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Chit getChit() {
        return chit;
    }

    public BigDecimal getIncomeAmount() {
        return incomeAmount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public Integer getNumberOfMonths() {
        return numberOfMonths;
    }

    public BigDecimal getInterestEarnedAmount() {
        return interestEarnedAmount;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(BigDecimal incomeAmount, BigDecimal percentage, Integer numberOfMonths,
            BigDecimal interestEarnedAmount) {
        this.incomeAmount = incomeAmount;
        this.percentage = percentage;
        this.numberOfMonths = numberOfMonths;
        this.interestEarnedAmount = interestEarnedAmount;
    }

    public void deactivate() {
        this.active = false;
    }
}
