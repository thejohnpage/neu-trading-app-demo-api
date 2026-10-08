package com.neueda.leap.trading.kafka;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminKafkaControllerMvcTest {
    private KafkaMonitoringService monitoring;
    private KafkaEventMonitoringService events;
    private MockMvc mvc;

    @BeforeEach void setup() {
        monitoring=mock(KafkaMonitoringService.class);
        events=mock(KafkaEventMonitoringService.class);
        mvc=MockMvcBuilders.standaloneSetup(new AdminKafkaController(monitoring,events)).build();
    }

    @Test void statusReturnsKafkaClusterStatus() throws Exception {
        when(monitoring.status()).thenReturn(new KafkaStatusResponse("DOWN",null,0,List.of(),List.of()));
        mvc.perform(get("/api/v1/admin/kafka/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DOWN"));
    }

    @Test void eventsDefaultLimitIsTwenty() throws Exception {
        when(events.recent(20)).thenReturn(List.of());
        mvc.perform(get("/api/v1/admin/kafka/events"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(events).recent(20);
    }

    @Test void eventsLimitIsClampedToOneHundred() throws Exception {
        when(events.recent(100)).thenReturn(List.of());
        mvc.perform(get("/api/v1/admin/kafka/events").param("limit","1000"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(events).recent(100);
    }
}
