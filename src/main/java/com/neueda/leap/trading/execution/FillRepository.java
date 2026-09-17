package com.neueda.leap.trading.execution;
import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface FillRepository extends JpaRepository<Fill,UUID> { boolean existsByOrderId(UUID orderId); }
