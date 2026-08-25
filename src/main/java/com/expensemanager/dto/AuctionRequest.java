package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AuctionRequest(
                @NotNull UUID chitId,
                @NotNull @Min(1) Integer bidNo,
                boolean extraHand,
                @NotNull LocalDate auctionMonth,
                @NotNull @DecimalMin("0.00") BigDecimal bidAmount,
                @NotNull UUID winningMemberId) {
}
