package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChitRequest(
        @NotBlank String name,
        @NotNull @DecimalMin("0.01") BigDecimal totalAmount,
        @NotNull @Min(1) Integer memberCount,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyInstallment,
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal agentPercentage,
        @NotNull LocalDate startDate,
        @NotNull @Min(1) Integer durationMonths) {
}
