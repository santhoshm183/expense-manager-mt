package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.expensemanager.entity.Auction;

public record AuctionResponse(UUID id, UUID chitId, String chitName, Integer bidNo, String handType,
        boolean partialAmount,
        LocalDate auctionMonth,
        BigDecimal bidAmount, UUID winningMemberId, String winningMemberName, BigDecimal netAmountPaid,
        BigDecimal agentAmount, BigDecimal profitAmount, BigDecimal profitValue) {
    public static AuctionResponse from(Auction auction) {
        return new AuctionResponse(auction.getId(), auction.getChit().getId(), auction.getChit().getName(),
                auction.getBidNo(), auction.getHandType(), auction.isPartialAmount(), auction.getAuctionMonth(),
                auction.getBidAmount(),
                auction.getWinningMember().getId(),
                auction.getWinningMember().getName(), auction.getNetAmountPaid(), auction.getAgentAmount(),
                auction.getProfitAmount(), auction.getProfitValue());
    }
}
