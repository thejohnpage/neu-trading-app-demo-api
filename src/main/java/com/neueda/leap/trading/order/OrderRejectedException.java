package com.neueda.leap.trading.order;

public class OrderRejectedException extends RuntimeException {
    public OrderRejectedException(String message) { super(message); }
}
