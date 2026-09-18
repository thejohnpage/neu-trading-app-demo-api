package com.neueda.leap.trading.identity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRegistrationRequest(
        @Email @NotBlank String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String clientSegment,
        @NotBlank @Size(min=10) String password) {}
