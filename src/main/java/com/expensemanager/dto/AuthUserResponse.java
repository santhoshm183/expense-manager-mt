package com.expensemanager.dto;

import java.util.UUID;

public record AuthUserResponse(
        UUID userId,
        String username,
        String role,
        UUID memberId,
        boolean enabled) {
}
