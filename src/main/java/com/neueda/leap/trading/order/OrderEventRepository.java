package com.neueda.leap.trading.order;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for order lifecycle events. */
@Mapper public interface OrderEventRepository {
 @Insert("INSERT INTO trading.order_events(order_id,event_type,event_time,details) VALUES(#{orderId},#{eventType},#{eventTime},CAST(#{details} AS jsonb))") int insert(OrderEvent e);
 @Select("SELECT order_event_id orderEventId,order_id orderId,event_type eventType,event_time eventTime,details::text details FROM trading.order_events WHERE order_id=#{orderId} ORDER BY event_time,order_event_id") List<OrderEvent> findByOrderIdOrderByEventTimeAscOrderEventIdAsc(UUID orderId);
}
