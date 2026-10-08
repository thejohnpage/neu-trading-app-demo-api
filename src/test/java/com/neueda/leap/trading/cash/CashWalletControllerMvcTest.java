package com.neueda.leap.trading.cash;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
class CashWalletControllerMvcTest {
 private CashWalletService service;
 private MockMvc mvc;
 @BeforeEach void setup(){
  service=mock(CashWalletService.class);
  mvc=MockMvcBuilders.standaloneSetup(new CashWalletController(service)).build();
 }
 @Test void depositReturnsBalance() throws Exception {
  UUID id=UUID.randomUUID();
  when(service.deposit(any())).thenReturn(new CashBalanceResponse(id,"USD",new BigDecimal("50"),Instant.now()));
  mvc.perform(post("/api/v1/me/cash/deposits").contentType("application/json")
    .content("{\"accountId\":\""+id+"\",\"currency\":\"USD\",\"amount\":50}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.currency").value("USD"))
    .andExpect(jsonPath("$.balance").value(50));
  verify(service).deposit(any());
 }
 @Test void withdrawalDelegatesToService() throws Exception {
  UUID id=UUID.randomUUID();
  when(service.withdraw(any())).thenReturn(new CashBalanceResponse(id,"USD",BigDecimal.ZERO,Instant.now()));
  mvc.perform(post("/api/v1/me/cash/withdrawals").contentType("application/json")
    .content("{\"accountId\":\""+id+"\",\"currency\":\"USD\",\"amount\":10}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(0));
 }
 @Test void rateReturnsQuote() throws Exception {
  when(service.rate("USD","GBP")).thenReturn(new FxRateResponse("USD","GBP",new BigDecimal("0.75"),"TEST",Instant.now()));
  mvc.perform(get("/api/v1/me/cash/rates").param("from","USD").param("to","GBP"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.rate").value(0.75));
 }
 @Test void transactionHistoryRequiresAccountId() throws Exception {
  mvc.perform(get("/api/v1/me/cash/transactions")).andExpect(status().isBadRequest());
  verify(service,never()).transactions(any());
 }
 @Test void transactionHistoryReturnsArray() throws Exception {
  UUID id=UUID.randomUUID();
  when(service.transactions(id)).thenReturn(List.of());
  mvc.perform(get("/api/v1/me/cash/transactions").param("accountId",id.toString()))
    .andExpect(status().isOk()).andExpect(content().json("[]"));
 }
}
