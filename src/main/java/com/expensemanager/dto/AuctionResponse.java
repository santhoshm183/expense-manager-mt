package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.expensemanager.entity.Auction;

public record AuctionResponse(UUID id, UUID chitId, String chitName, Integer bidNo, boolean extraHand,
        LocalDate auctionMonth,
        BigDecimal bidAmount, UUID winningMemberId, String winningMemberName, BigDecimal netAmountPaid,
        BigDecimal agentAmount, BigDecimal profitAmount) {
    public static AuctionResponse from(Auction auction) {
        return new AuctionResponse(auction.getId(), auction.getChit().getId(), auction.getChit().getName(),
                auction.getBidNo(), auction.isExtraHand(), auction.getAuctionMonth(), auction.getBidAmount(),
                auction.getWinningMember().getId(),
                auction.getWinningMember().getName(), auction.getNetAmountPaid(), auction.getAgentAmount(),
                auction.getProfitAmount());
    }
}
