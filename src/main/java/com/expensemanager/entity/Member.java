package com.expensemanager.entity;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chit_id", nullable = false)
    private Chit chit;
    @Column(nullable = false)
    private String name;
    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;
    private String email;
    @Column(name = "permanent_address")
    private String permanentAddress;
    @Column(name = "chit_taken", nullable = false)
    private boolean chitTaken;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Member() {
    }

    public Member(Chit chit, String name, String mobileNumber, String email, String permanentAddress) {
        this.chit = chit;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.permanentAddress = permanentAddress;
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

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public boolean isChitTaken() {
        return chitTaken;
    }

    public void markChitTaken(boolean chitTaken) {
        this.chitTaken = chitTaken;
    }

    public void update(Chit chit, String name, String mobileNumber, String email, String permanentAddress) {
        this.chit = chit;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.permanentAddress = permanentAddress;
    }
}
