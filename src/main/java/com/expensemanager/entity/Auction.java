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
@Table(name = "chit_auctions")
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chit_id", nullable = false)
    private Chit chit;
    @Column(name = "bid_no", nullable = false)
    private Integer bidNo;
    @Column(name = "hand_type", nullable = false)
    private String handType;
    @Column(name = "partial_amount", nullable = false)
    private boolean partialAmount;
    @Column(name = "auction_month", nullable = false)
    private LocalDate auctionMonth;
    @Column(name = "bid_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal bidAmount;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winning_member_id", nullable = false)
    private Member winningMember;
    @Column(name = "net_amount_paid", nullable = false, precision = 14, scale = 2)
    private BigDecimal netAmountPaid;
    @Column(name = "agent_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal agentAmount;
    @Column(name = "profit_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal profitAmount;
    @Column(name = "profit_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal profitValue;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Auction() {
    }

    public Auction(Chit chit, Integer bidNo, String handType, boolean partialAmount, LocalDate auctionMonth,
            BigDecimal bidAmount,
            Member winningMember,
            BigDecimal netAmountPaid, BigDecimal agentAmount, BigDecimal profitAmount, BigDecimal profitValue) {
        this.chit = chit;
        this.bidNo = bidNo;
        this.handType = handType;
        this.partialAmount = partialAmount;
        this.auctionMonth = auctionMonth;
        this.bidAmount = bidAmount;
        this.winningMember = winningMember;
        this.netAmountPaid = netAmountPaid;
        this.agentAmount = agentAmount;
        this.profitAmount = profitAmount;
        this.profitValue = profitValue;
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

    public Integer getBidNo() {
        return bidNo;
    }

    public LocalDate getAuctionMonth() {
        return auctionMonth;
    }

    public String getHandType() {
        return handType;
    }

    public boolean isPartialAmount() {
        return partialAmount;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public Member getWinningMember() {
        return winningMember;
    }

    public BigDecimal getNetAmountPaid() {
        return netAmountPaid;
    }

    public BigDecimal getAgentAmount() {
        return agentAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitValue() {
        return profitValue;
    }

    public void setProfitValue(BigDecimal profitValue) {
        this.profitValue = profitValue;
    }

    public void update(Chit chit, Integer bidNo, String handType, boolean partialAmount, LocalDate auctionMonth,
            BigDecimal bidAmount,
            Member winningMember,
            BigDecimal netAmountPaid, BigDecimal agentAmount, BigDecimal profitAmount, BigDecimal profitValue) {
        this.chit = chit;
        this.bidNo = bidNo;
        this.handType = handType;
        this.partialAmount = partialAmount;
        this.auctionMonth = auctionMonth;
        this.bidAmount = bidAmount;
        this.winningMember = winningMember;
        this.netAmountPaid = netAmountPaid;
        this.agentAmount = agentAmount;
        this.profitAmount = profitAmount;
        this.profitValue = profitValue;
    }
}
