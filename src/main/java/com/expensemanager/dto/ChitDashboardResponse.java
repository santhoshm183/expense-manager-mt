package com.expensemanager.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ChitDashboardResponse(
        UUID chitId,
        BigDecimal totalCollectionOfMonth,
        BigDecimal availableBalance,
        long membersNotPaid,
        Integer latestHand,
        long handsReleased,
        long extraHands,
        BigDecimal amountDistributed,
        BigDecimal agentCommission,
        BigDecimal investmentIncome,
        BigDecimal totalInvestmentIncome) {
}
