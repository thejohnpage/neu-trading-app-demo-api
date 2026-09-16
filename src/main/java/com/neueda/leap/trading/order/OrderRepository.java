package com.neueda.leap.trading.order;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByAccountIdInOrderBySubmittedAtDesc(List<UUID> accountIds);
}
