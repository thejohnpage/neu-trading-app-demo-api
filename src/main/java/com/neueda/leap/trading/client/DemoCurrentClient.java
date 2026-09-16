package com.neueda.leap.trading.client;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.auth.mode", havingValue = "demo", matchIfMissing = true)
public class DemoCurrentClient implements CurrentClient {

    // Joanna fixture from V2__demo_seed.sql. This class is replaced by the JWT
    // implementation when the NestJS auth API integration is enabled.
    public static final UUID JOANNA_CLIENT_ID =
            UUID.fromString("10000000-0000-0000-0000-000000000001");

    @Override
    public UUID clientId() {
        return JOANNA_CLIENT_ID;
    }
}
