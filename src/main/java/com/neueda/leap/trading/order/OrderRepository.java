package com.neueda.leap.trading.order;
import java.util.Collection; import java.util.List; import java.util.Optional; import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order,UUID>{
 List<Order> findByAccountIdInOrderBySubmittedAtDesc(Collection<UUID> accountIds);
 Optional<Order> findByOrderIdAndAccountIdIn(UUID orderId,Collection<UUID> accountIds);
}
