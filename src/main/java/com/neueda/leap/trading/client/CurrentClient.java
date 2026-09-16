package com.neueda.leap.trading.client;

import java.util.UUID;

/**
 * Boundary between the trading domain and authentication.
 *
 * The production/demo integration will resolve this value from the JWT issued
 * by the separate NestJS authentication API. Trading services depend only on
 * this abstraction and do not authenticate credentials themselves.
 */
public interface CurrentClient {
    UUID clientId();
}
