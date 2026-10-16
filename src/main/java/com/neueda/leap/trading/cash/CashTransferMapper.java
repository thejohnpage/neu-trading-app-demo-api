package com.neueda.leap.trading.cash;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CashTransferMapper {
    @Select("""
        SELECT account_id FROM trading.accounts
        WHERE account_id IN (
            #{fromId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{toId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
        ) ORDER BY account_id FOR UPDATE
        """)
    List<UUID> lockAccounts(@Param("fromId") UUID fromId, @Param("toId") UUID toId);

    @Select("""
        SELECT transfer_id AS transferId, from_account_id AS fromAccountId,
            to_account_id AS toAccountId, from_currency AS fromCurrency,
            to_currency AS toCurrency, debited_amount AS debitedAmount,
            credited_amount AS creditedAmount, exchange_rate AS exchangeRate,
            rate_source AS rateSource, created_at AS createdAt
        FROM trading.cash_transfers
        WHERE transfer_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND client_id=#{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
        """)
    CashTransferResponse find(@Param("id") UUID id, @Param("clientId") UUID clientId);

    @Insert("""
        INSERT INTO trading.cash_transfers(
            transfer_id,client_id,from_account_id,to_account_id,from_currency,to_currency,
            debited_amount,credited_amount,exchange_rate,rate_source,created_at)
        VALUES(
            #{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{fromId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{toId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{fromCurrency},#{toCurrency},#{debit},#{credit},#{rate},#{source},#{now})
        """)
    int insert(@Param("id") UUID id, @Param("clientId") UUID clientId,
               @Param("fromId") UUID fromId, @Param("toId") UUID toId,
               @Param("fromCurrency") String fromCurrency, @Param("toCurrency") String toCurrency,
               @Param("debit") BigDecimal debit, @Param("credit") BigDecimal credit,
               @Param("rate") BigDecimal rate, @Param("source") String source, @Param("now") Instant now);

    @Update("""
        UPDATE trading.cash_balances
        SET balance=balance-#{amount}, version=version+1, updated_at=#{now}
        WHERE account_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
          AND currency=#{currency} AND balance>=#{amount}
        """)
    int debit(@Param("id") UUID id, @Param("currency") String currency,
              @Param("amount") BigDecimal amount, @Param("now") Instant now);

    @Insert("""
        INSERT INTO trading.cash_balances(account_id,currency,balance,updated_at,version)
        VALUES(#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{currency},#{amount},#{now},0)
        ON CONFLICT (account_id,currency) DO UPDATE SET
          balance=trading.cash_balances.balance+EXCLUDED.balance,
          updated_at=EXCLUDED.updated_at,
          version=trading.cash_balances.version+1
        """)
    int credit(@Param("id") UUID id, @Param("currency") String currency,
               @Param("amount") BigDecimal amount, @Param("now") Instant now);

    @Insert("""
        INSERT INTO trading.cash_transactions(
            cash_transaction_id,account_id,order_id,transfer_id,currency,amount,transaction_type,created_at)
        VALUES(
            #{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{accountId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            NULL,#{transferId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},
            #{currency},#{amount},#{type},#{now})
        """)
    int ledger(@Param("id") UUID id, @Param("accountId") UUID accountId,
               @Param("transferId") UUID transferId, @Param("currency") String currency,
               @Param("amount") BigDecimal amount, @Param("type") String type,
               @Param("now") Instant now);
}
