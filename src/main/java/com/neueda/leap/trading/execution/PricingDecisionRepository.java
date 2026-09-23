package com.neueda.leap.trading.execution;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for auditable pricing decisions. */
@Mapper public interface PricingDecisionRepository {
 @Insert("INSERT INTO audit.pricing_decisions(pricing_decision_id,order_id,instrument_id,bid_price,ask_price,execution_price,quote_source,quote_timestamp,priced_at) VALUES(#{pricingDecisionId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{orderId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{instrumentId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{bidPrice},#{askPrice},#{executionPrice},#{quoteSource},#{quoteTimestamp},#{pricedAt})") int insert(PricingDecision p);
 @Select("SELECT pricing_decision_id pricingDecisionId,order_id orderId,instrument_id instrumentId,bid_price bidPrice,ask_price askPrice,execution_price executionPrice,quote_source quoteSource,quote_timestamp quoteTimestamp,priced_at pricedAt FROM audit.pricing_decisions WHERE order_id=#{orderId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}") Optional<PricingDecision> findByOrderId(java.util.UUID orderId);
}
