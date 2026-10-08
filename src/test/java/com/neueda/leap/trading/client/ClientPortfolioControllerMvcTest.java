package com.neueda.leap.trading.client;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
class ClientPortfolioControllerMvcTest {
 private ClientPortfolioService service;
 private MockMvc mvc;
 @BeforeEach void setup(){
  service=mock(ClientPortfolioService.class);
  mvc=MockMvcBuilders.standaloneSetup(new ClientPortfolioController(service)).build();
 }
 @Test void accountsRouteReturnsJsonArray() throws Exception {
  when(service.accounts()).thenReturn(List.of());
  mvc.perform(get("/api/v1/me/accounts")).andExpect(status().isOk()).andExpect(content().json("[]"));
  verify(service).accounts();
 }
 @Test void cashRouteReturnsJsonArray() throws Exception {
  when(service.cash()).thenReturn(List.of());
  mvc.perform(get("/api/v1/me/cash")).andExpect(status().isOk()).andExpect(content().json("[]"));
  verify(service).cash();
 }
 @Test void positionsRouteReturnsJsonArray() throws Exception {
  when(service.positions()).thenReturn(List.of());
  mvc.perform(get("/api/v1/me/positions")).andExpect(status().isOk()).andExpect(content().json("[]"));
  verify(service).positions();
 }
}
