package com.expensemanager.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private UUID id;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TransactionType transactionType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Transaction() {
    }

    public Transaction(UUID id, LocalDate transactionDate, String description, BigDecimal amount,
            TransactionType transactionType) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void update(LocalDate transactionDate, String description, BigDecimal amount,
            TransactionType transactionType) {
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}