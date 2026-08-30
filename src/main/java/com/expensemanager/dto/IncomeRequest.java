package com.expensemanager.dto;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record IncomeRequest(
        @NotNull UUID chitId,
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal percentage,
        @NotNull @Min(1) @Max(12) Integer numberOfMonths) {
}
