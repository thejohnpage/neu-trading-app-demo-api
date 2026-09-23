package com.neueda.leap.trading.position;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for positions with explicit optimistic locking. */
@Mapper public interface PositionRepository {
 @Select("SELECT account_id accountId,instrument_id instrumentId,quantity,cost_basis costBasis,updated_at updatedAt,version FROM trading.positions WHERE account_id=#{accountId} AND instrument_id=#{instrumentId}") Optional<Position> find(@Param("accountId") UUID accountId,@Param("instrumentId") UUID instrumentId);
 @Insert("INSERT INTO trading.positions(account_id,instrument_id,quantity,cost_basis,updated_at,version) VALUES(#{accountId},#{instrumentId},#{quantity},#{costBasis},#{updatedAt},0)") int insert(Position p);
 @Update("UPDATE trading.positions SET quantity=#{quantity},cost_basis=#{costBasis},updated_at=#{updatedAt},version=version+1 WHERE account_id=#{accountId} AND instrument_id=#{instrumentId} AND version=#{version}") int update(Position p);
}
