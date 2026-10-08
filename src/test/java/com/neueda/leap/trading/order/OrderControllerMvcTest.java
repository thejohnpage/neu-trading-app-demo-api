package com.neueda.leap.trading.order;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.UUID;
import com.neueda.leap.trading.api.ApiExceptionHandler;
import com.neueda.leap.trading.api.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
class OrderControllerMvcTest {
 private OrderService commands;
 private OrderQueryService queries;
 private MockMvc mvc;
 @BeforeEach void setup(){
  commands=mock(OrderService.class);
  queries=mock(OrderQueryService.class);
  mvc=MockMvcBuilders.standaloneSetup(new OrderController(commands,queries))
    .setControllerAdvice(new ApiExceptionHandler()).build();
 }
 @Test void listsOrders() throws Exception {
  when(queries.findAll()).thenReturn(List.of());
  mvc.perform(get("/api/v1/orders")).andExpect(status().isOk()).andExpect(content().json("[]"));
  verify(queries).findAll();
 }
 @Test void returns404ForMissingOwnedOrder() throws Exception {
  UUID id=UUID.randomUUID();
  when(queries.findOne(id)).thenThrow(new ResourceNotFoundException("Order not found"));
  mvc.perform(get("/api/v1/orders/{id}",id)).andExpect(status().isNotFound());
 }
 @Test void returnsOrderEvents() throws Exception {
  UUID id=UUID.randomUUID();
  when(queries.lifecycle(id)).thenReturn(List.of());
  mvc.perform(get("/api/v1/orders/{id}/events",id))
    .andExpect(status().isOk()).andExpect(content().json("[]"));
  verify(queries).lifecycle(id);
 }
 @Test void rejectsMalformedOrderRequest() throws Exception {
  mvc.perform(post("/api/v1/orders").contentType("application/json").content("{"))
    .andExpect(status().isBadRequest());
  verifyNoInteractions(commands);
 }
}
