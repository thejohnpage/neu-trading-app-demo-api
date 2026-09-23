package com.neueda.leap.trading.execution;
import java.util.UUID; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for fills. */
@Mapper public interface FillRepository {
 @Select("SELECT EXISTS(SELECT 1 FROM trading.fills WHERE order_id=#{orderId})") boolean existsByOrderId(UUID orderId);
 @Insert("INSERT INTO trading.fills(fill_id,order_id,quantity,price,filled_at) VALUES(#{fillId},#{orderId},#{quantity},#{price},#{filledAt})") int insert(Fill f);
}
