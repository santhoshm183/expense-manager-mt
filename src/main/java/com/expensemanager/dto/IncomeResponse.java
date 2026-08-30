package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.expensemanager.entity.Income;

public record IncomeResponse(
        UUID id,
        UUID chitId,
        BigDecimal incomeAmount,
        BigDecimal percentage,
        Integer numberOfMonths,
        BigDecimal interestEarnedAmount,
        Instant createdAt,
        boolean active) {
    public static IncomeResponse from(Income income) {
        return new IncomeResponse(income.getId(), income.getChit().getId(), income.getIncomeAmount(),
                income.getPercentage(), income.getNumberOfMonths(), income.getInterestEarnedAmount(),
                income.getCreatedAt(), income.isActive());
    }
}
