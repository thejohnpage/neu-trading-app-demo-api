package com.neueda.leap.trading.execution;
import org.apache.ibatis.annotations.*;
/** MyBatis mapper for auditable pricing decisions. */
@Mapper public interface PricingDecisionRepository {
 @Insert("INSERT INTO audit.pricing_decisions(pricing_decision_id,order_id,instrument_id,bid_price,ask_price,execution_price,quote_source,quote_timestamp,priced_at) VALUES(#{pricingDecisionId},#{orderId},#{instrumentId},#{bidPrice},#{askPrice},#{executionPrice},#{quoteSource},#{quoteTimestamp},#{pricedAt})") int insert(PricingDecision p);
}
