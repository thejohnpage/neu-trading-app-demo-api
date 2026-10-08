package com.neueda.leap.trading.reporting;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
class ReportingControllerMvcTest {
 private ReportingService service;
 private MockMvc mvc;
 @BeforeEach void setup(){
  service=mock(ReportingService.class);
  mvc=MockMvcBuilders.standaloneSetup(new ReportingController(service)).build();
 }
 @Test void summaryReturnsWarehouseSummary() throws Exception {
  when(service.summary()).thenReturn(Map.of("count",3));
  mvc.perform(get("/api/v1/admin/reports/summary")).andExpect(status().isOk()).andExpect(jsonPath("$.count").value(3));
  verify(service).summary();
 }
 @Test void activityReturnsArray() throws Exception {
  when(service.activity()).thenReturn(List.of());
  mvc.perform(get("/api/v1/admin/reports/activity")).andExpect(status().isOk()).andExpect(content().json("[]"));
 }
 @Test void instrumentsReturnsArray() throws Exception {
  when(service.instruments()).thenReturn(List.of());
  mvc.perform(get("/api/v1/admin/reports/instruments")).andExpect(status().isOk()).andExpect(content().json("[]"));
 }
 @Test void clientSegmentsReturnsArray() throws Exception {
  when(service.clientSegments()).thenReturn(List.of());
  mvc.perform(get("/api/v1/admin/reports/client-segments")).andExpect(status().isOk()).andExpect(content().json("[]"));
 }
 @Test void volumeReturnsArray() throws Exception {
  when(service.volume()).thenReturn(List.of());
  mvc.perform(get("/api/v1/admin/reports/volume")).andExpect(status().isOk()).andExpect(content().json("[]"));
 }
}
