package com.neueda.leap.trading.account;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for trading accounts. */
@Mapper public interface AccountRepository {
 @Select("SELECT account_id accountId,client_id clientId,account_number accountNumber,base_currency baseCurrency,status,created_at createdAt FROM trading.accounts WHERE client_id=#{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler} ORDER BY account_number") List<Account> findByClientIdOrderByAccountNumber(UUID clientId);
 @Select("SELECT account_id accountId,client_id clientId,account_number accountNumber,base_currency baseCurrency,status,created_at createdAt FROM trading.accounts WHERE account_id=#{accountId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler} AND client_id=#{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}") Optional<Account> findByAccountIdAndClientId(@Param("accountId") UUID accountId,@Param("clientId") UUID clientId);
}
