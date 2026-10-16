package com.neueda.leap.trading.account;

import java.util.UUID;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AccountManagementMapper {
    @Insert("""
        INSERT INTO trading.accounts(account_id, client_id, account_number, account_name, account_type, base_currency, status)
        VALUES(#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
               #{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
               #{number}, #{name}, 'INDIVIDUAL', #{currency}, 'ACTIVE')
        """)
    int create(@Param("id") UUID id, @Param("clientId") UUID clientId,
               @Param("number") String number, @Param("name") String name,
               @Param("currency") String currency);

    @Insert("""
        INSERT INTO trading.cash_balances(account_id, currency, balance)
        VALUES(#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}, #{currency}, 0)
        """)
    int initialCash(@Param("id") UUID id, @Param("currency") String currency);

    @Update("""
        UPDATE trading.accounts SET account_name=#{name}
        WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND client_id=#{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND status='ACTIVE'
        """)
    int rename(@Param("id") UUID id, @Param("clientId") UUID clientId, @Param("name") String name);

    @Select("""
        SELECT NOT EXISTS (
          SELECT 1 FROM trading.cash_balances
          WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
            AND balance <> 0
        ) AND NOT EXISTS (
          SELECT 1 FROM trading.positions
          WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
            AND quantity <> 0
        ) AND NOT EXISTS (
          SELECT 1 FROM trading.orders
          WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
            AND status IN ('SUBMITTED','ACCEPTED')
        )
        """)
    boolean canClose(UUID id);

    @Update("""
        UPDATE trading.accounts SET status='CLOSED'
        WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND client_id=#{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND status='ACTIVE'
        """)
    int close(@Param("id") UUID id, @Param("clientId") UUID clientId);
}
