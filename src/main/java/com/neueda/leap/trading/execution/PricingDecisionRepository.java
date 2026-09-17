package com.neueda.leap.trading.execution;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PricingDecisionRepository extends JpaRepository<PricingDecision,UUID> {}
