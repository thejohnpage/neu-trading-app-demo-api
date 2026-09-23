package com.neueda.leap.trading.order;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for orders. */
@Mapper public interface OrderRepository {
 String COLS="order_id orderId,account_id accountId,instrument_id instrumentId,side,quantity,status,rejection_reason rejectionReason,submitted_at submittedAt,accepted_at acceptedAt,completed_at completedAt,version";
 @Select("SELECT "+COLS+" FROM trading.orders WHERE order_id=#{id}") Optional<Order> findById(UUID id);
 @Select("SELECT "+COLS+" FROM trading.orders ORDER BY submitted_at DESC") List<Order> findAll();
 @Select("<script>SELECT "+COLS+" FROM trading.orders WHERE account_id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY submitted_at DESC</script>") List<Order> findByAccountIds(@Param("ids") Collection<UUID> ids);
 @Select("<script>SELECT "+COLS+" FROM trading.orders WHERE order_id=#{orderId} AND account_id IN <foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach></script>") Optional<Order> findOwned(@Param("orderId") UUID orderId,@Param("ids") Collection<UUID> ids);
 @Insert("INSERT INTO trading.orders(order_id,account_id,instrument_id,side,quantity,status,rejection_reason,submitted_at,accepted_at,completed_at,version) VALUES(#{orderId},#{accountId},#{instrumentId},#{side},#{quantity},#{status},#{rejectionReason},#{submittedAt},#{acceptedAt},#{completedAt},0)") int insert(Order o);
 @Update("UPDATE trading.orders SET status=#{status},completed_at=#{completedAt},version=version+1 WHERE order_id=#{orderId} AND version=#{version}") int update(Order o);
}
