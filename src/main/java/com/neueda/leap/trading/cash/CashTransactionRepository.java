package com.neueda.leap.trading.cash;
import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for the cash transaction ledger. */
@Mapper public interface CashTransactionRepository {
 @Insert("INSERT INTO trading.cash_transactions(cash_transaction_id,account_id,order_id,currency,amount,transaction_type,created_at) VALUES(#{id},#{accountId},#{orderId},#{currency},#{amount},#{type},#{createdAt})") int insert(CashTransaction t);
 @Select("SELECT cash_transaction_id id,account_id accountId,order_id orderId,currency,amount,transaction_type type,created_at createdAt FROM trading.cash_transactions WHERE account_id=#{accountId} ORDER BY created_at DESC") List<CashTransaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
