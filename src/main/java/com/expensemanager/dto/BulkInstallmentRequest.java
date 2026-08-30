package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record BulkInstallmentRequest(
        @NotNull UUID chitId,
        @NotEmpty List<UUID> memberIds,
        @NotNull @Min(1) Integer numberOfHand,
        @NotNull @DecimalMin("0.01") BigDecimal installmentAmount,
        @NotNull LocalDate installmentDate) {
}
