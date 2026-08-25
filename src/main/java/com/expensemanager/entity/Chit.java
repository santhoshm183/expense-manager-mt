package com.expensemanager.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "chits")
public class Chit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "chit_id", nullable = false, unique = true)
    private String chitId;
    @Column(nullable = false)
    private String name;
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;
    @Column(name = "member_count", nullable = false)
    private Integer memberCount;
    @Column(name = "monthly_installment", nullable = false, precision = 14, scale = 2)
    private BigDecimal monthlyInstallment;
    @Column(name = "agent_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal agentPercentage;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private ChitStatus status = ChitStatus.active;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Chit() {
    }

    public Chit(String chitId, String name, BigDecimal totalAmount, Integer memberCount,
            BigDecimal monthlyInstallment, BigDecimal agentPercentage, LocalDate startDate, Integer durationMonths) {
        this.chitId = chitId;
        this.name = name;
        this.totalAmount = totalAmount;
        this.memberCount = memberCount;
        this.monthlyInstallment = monthlyInstallment;
        this.agentPercentage = agentPercentage;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
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

    public String getChitId() {
        return chitId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public BigDecimal getAgentPercentage() {
        return agentPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public ChitStatus getStatus() {
        return status;
    }

    public void update(String name, BigDecimal totalAmount, Integer memberCount, BigDecimal monthlyInstallment,
            BigDecimal agentPercentage, LocalDate startDate, Integer durationMonths) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.memberCount = memberCount;
        this.monthlyInstallment = monthlyInstallment;
        this.agentPercentage = agentPercentage;
        this.startDate = startDate;
        this.durationMonths = durationMonths;
    }
}
