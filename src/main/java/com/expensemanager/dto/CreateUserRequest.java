package com.expensemanager.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password,
        @NotBlank(message = "Role is required") String role,
        UUID memberId) {
}
