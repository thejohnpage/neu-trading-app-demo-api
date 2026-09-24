package com.neueda.leap.trading.client;

import java.util.List;
import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis read mapper for the client portfolio.
 *
 * <p>Result objects are conventional JavaBeans so MyBatis can use predictable
 * setter-based property mapping rather than record-constructor inference.</p>
 */
@Mapper
public interface ClientPortfolioMapper {

    /** Returns accounts owned by a client in account-number order. */
    @Select("""
        SELECT account_id, account_number,
               base_currency, status
        FROM trading.accounts
        WHERE client_id = #{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
        ORDER BY account_number
        """)
    List<AccountRow> accounts(@Param("clientId") UUID clientId);

    /** Returns cash balances for all accounts owned by a client. */
    @Select("""
        SELECT cb.account_id, cb.currency, cb.balance, cb.updated_at
        FROM trading.cash_balances cb
        JOIN trading.accounts a ON a.account_id = cb.account_id
        WHERE a.client_id = #{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
        ORDER BY cb.account_id, cb.currency
        """)
    List<CashRow> cash(@Param("clientId") UUID clientId);

    /** Returns positions together with the latest available bid quote. */
    @Select("""
        SELECT p.account_id, p.instrument_id,
               i.symbol, i.instrument_type, i.quote_currency AS currency,
               p.quantity, p.cost_basis,
               COALESCE(q.bid_price, 0) AS current_price,
               p.updated_at
        FROM trading.positions p
        JOIN trading.accounts a ON a.account_id = p.account_id
        JOIN trading.instruments i ON i.instrument_id = p.instrument_id
        LEFT JOIN LATERAL (
            SELECT mq.bid_price
            FROM trading.market_quotes mq
            WHERE mq.instrument_id = p.instrument_id
            ORDER BY mq.quoted_at DESC
            LIMIT 1
        ) q ON TRUE
        WHERE a.client_id = #{clientId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}
        ORDER BY p.account_id, i.symbol
        """)
    List<PositionRow> positions(@Param("clientId") UUID clientId);
}
