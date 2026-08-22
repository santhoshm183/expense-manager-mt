package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expensemanager.entity.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
                @NotNull LocalDate date,
                @jakarta.validation.constraints.NotBlank String description,
                @NotNull @DecimalMin("0.01") BigDecimal amount,
                @NotNull TransactionType type) {
}