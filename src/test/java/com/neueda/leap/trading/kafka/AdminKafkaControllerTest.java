package com.neueda.leap.trading.kafka;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class AdminKafkaControllerTest {
    private final KafkaMonitoringService monitoring=mock(KafkaMonitoringService.class);
    private final KafkaEventMonitoringService events=mock(KafkaEventMonitoringService.class);
    private final AdminKafkaController controller=new AdminKafkaController(monitoring,events);

    @Test void statusDelegatesToMonitoringService() {
        KafkaStatusResponse status=new KafkaStatusResponse("DOWN",null,0,List.of(),List.of());
        when(monitoring.status()).thenReturn(status);
        assertSame(status,controller.status());
        verify(monitoring).status();
    }

    @Test void eventsClampsPageSizeBetweenOneAndOneHundred() {
        when(events.recent(anyInt())).thenReturn(List.of());
        assertTrue(controller.events(-5).isEmpty());
        assertTrue(controller.events(200).isEmpty());
        assertTrue(controller.events(20).isEmpty());
        verify(events).recent(1);
        verify(events).recent(100);
        verify(events).recent(20);
    }
}
