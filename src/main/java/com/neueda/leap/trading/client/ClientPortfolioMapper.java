package com.neueda.leap.trading.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis read mapper for the client portfolio.
 *
 * <p>The trading command side remains JPA-backed so aggregate updates continue
 * to use optimistic locking, while portfolio/read-model queries use explicit
 * SQL through MyBatis.</p>
 */
@Mapper
public interface ClientPortfolioMapper {

    /** Returns accounts owned by a client in account-number order. */
    @Select("""
        SELECT account_id AS accountId, account_number AS accountNumber,
               base_currency AS baseCurrency, status
        FROM trading.accounts
        WHERE client_id = #{clientId}
        ORDER BY account_number
        """)
    List<AccountRow> accounts(@Param("clientId") UUID clientId);

    /** Returns cash balances for all accounts owned by a client. */
    @Select("""
        SELECT cb.account_id AS accountId, cb.currency, cb.balance, cb.updated_at AS updatedAt
        FROM trading.cash_balances cb
        JOIN trading.accounts a ON a.account_id = cb.account_id
        WHERE a.client_id = #{clientId}
        ORDER BY cb.account_id, cb.currency
        """)
    List<CashRow> cash(@Param("clientId") UUID clientId);

    /** Returns positions together with the latest bid used for portfolio valuation. */
    @Select("""
        SELECT p.account_id AS accountId, p.instrument_id AS instrumentId,
               i.symbol, i.instrument_type AS instrumentType, i.quote_currency AS currency,
               p.quantity, p.cost_basis AS costBasis, p.updated_at AS updatedAt,
               q.bid_price AS currentPrice
        FROM trading.positions p
        JOIN trading.accounts a ON a.account_id = p.account_id
        JOIN trading.instruments i ON i.instrument_id = p.instrument_id
        JOIN LATERAL (
            SELECT mq.bid_price
            FROM trading.market_quotes mq
            WHERE mq.instrument_id = p.instrument_id
            ORDER BY mq.quoted_at DESC
            LIMIT 1
        ) q ON TRUE
        WHERE a.client_id = #{clientId}
        ORDER BY p.account_id, i.symbol
        """)
    List<PositionRow> positions(@Param("clientId") UUID clientId);

    /** Lightweight account projection returned by MyBatis. */
    record AccountRow(UUID accountId,String accountNumber,String baseCurrency,String status) {}
    /** Lightweight cash projection returned by MyBatis. */
    record CashRow(UUID accountId,String currency,BigDecimal balance,Instant updatedAt) {}
    /** Lightweight valued-position source row returned by MyBatis. */
    record PositionRow(UUID accountId,UUID instrumentId,String symbol,String instrumentType,String currency,
                       BigDecimal quantity,BigDecimal costBasis,BigDecimal currentPrice,Instant updatedAt) {}
}
