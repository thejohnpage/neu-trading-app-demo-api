package com.neueda.leap.trading.cash;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for cash balances with explicit optimistic locking. */
@Mapper public interface CashBalanceRepository {
 @Select("SELECT account_id accountId,currency,balance,updated_at updatedAt,version FROM trading.cash_balances WHERE account_id=#{accountId} AND currency=#{currency}") Optional<CashBalance> find(@Param("accountId") UUID accountId,@Param("currency") String currency);
 @Insert("INSERT INTO trading.cash_balances(account_id,currency,balance,updated_at,version) VALUES(#{accountId},#{currency},#{balance},#{updatedAt},0)") int insert(CashBalance b);
 @Update("UPDATE trading.cash_balances SET balance=#{balance},updated_at=#{updatedAt},version=version+1 WHERE account_id=#{accountId} AND currency=#{currency} AND version=#{version}") int update(CashBalance b);
}
