package com.expensemanager.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "installment")
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chit_id", nullable = false)
    private Chit chit;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Column(name = "number_of_hand", nullable = false)
    private Integer numberOfHand;
    @Column(name = "installment_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal installmentAmount;
    @Column(name = "installment_date", nullable = false)
    private LocalDate installmentDate;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Installment() {
    }

    public Installment(Chit chit, Member member, Integer numberOfHand, BigDecimal installmentAmount,
            LocalDate installmentDate) {
        this.chit = chit;
        this.member = member;
        this.numberOfHand = numberOfHand;
        this.installmentAmount = installmentAmount;
        this.installmentDate = installmentDate;
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

    public Member getMember() {
        return member;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public Integer getNumberOfHand() {
        return numberOfHand;
    }

    public LocalDate getInstallmentDate() {
        return installmentDate;
    }

    public void update(Chit chit, Member member, Integer numberOfHand, BigDecimal installmentAmount,
            LocalDate installmentDate) {
        this.chit = chit;
        this.member = member;
        this.numberOfHand = numberOfHand;
        this.installmentAmount = installmentAmount;
        this.installmentDate = installmentDate;
    }
}
