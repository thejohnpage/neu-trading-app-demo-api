package com.neueda.leap.trading.identity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        boolean active,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt) {
}
