package com.neueda.leap.trading.order;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull UUID accountId,
        @NotBlank String symbol,
        @NotBlank String side,
        @NotNull @DecimalMin(value="0.00000001") BigDecimal quantity) {
}
