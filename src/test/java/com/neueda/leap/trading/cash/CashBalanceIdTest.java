package com.neueda.leap.trading.cash;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
class CashBalanceIdTest {
 @Test void equalityDependsOnAccountAndCurrency() {
  UUID account=UUID.randomUUID();
  CashBalanceId a=new CashBalanceId(account,"USD");
  CashBalanceId same=new CashBalanceId(account,"USD");
  assertEquals(a,a);
  assertEquals(a,same);
  assertEquals(a.hashCode(),same.hashCode());
  assertNotEquals(a,new CashBalanceId(account,"GBP"));
  assertNotEquals(a,new CashBalanceId(UUID.randomUUID(),"USD"));
  assertNotEquals(a,"other");
  assertNotEquals(a,null);
 }
 @Test void emptyInstancesCompareEqual() {
  assertEquals(new CashBalanceId(),new CashBalanceId());
 }
}
