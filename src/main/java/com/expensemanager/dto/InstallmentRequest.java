package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record InstallmentRequest(
        @NotNull UUID chitId,
        @NotNull UUID memberId,
        @NotNull @Min(1) Integer numberOfHand,
        @NotNull @DecimalMin("0.01") BigDecimal installmentAmount,
        @NotNull LocalDate installmentDate) {
}
