package com.neueda.leap.trading.execution;
import java.util.UUID; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for fills. */
@Mapper public interface FillRepository {
 @Select("SELECT EXISTS(SELECT 1 FROM trading.fills WHERE order_id=#{orderId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler})") boolean existsByOrderId(UUID orderId);
 @Insert("INSERT INTO trading.fills(fill_id,order_id,quantity,price,filled_at) VALUES(#{fillId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{orderId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{quantity},#{price},#{filledAt})") int insert(Fill f);
}
