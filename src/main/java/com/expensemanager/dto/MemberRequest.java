package com.expensemanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRequest(
                @NotNull UUID chitId,
                @NotBlank String name,
                @NotBlank String mobileNumber,
                String email,
                String permanentAddress,
                String username,
                String password) {
}
