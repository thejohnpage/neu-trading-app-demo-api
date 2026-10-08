package com.neueda.leap.trading.execution;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import com.neueda.leap.trading.order.OutboxEvent;
import com.neueda.leap.trading.order.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

class OutboxPublisherTest {
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String,String> kafka=mock(KafkaTemplate.class);
    private final OutboxEventRepository outbox=mock(OutboxEventRepository.class);
    private final ObjectMapper json=mock(ObjectMapper.class);
    private final OutboxPublisher publisher=new OutboxPublisher(outbox,kafka,json);

    @Test void emptyOutboxDoesNotPublish() {
        when(outbox.findUnpublished()).thenReturn(List.of());
        publisher.publish();
        verifyNoInteractions(kafka,json);
    }

    @Test void unrelatedEventIsNotPublished() {
        OutboxEvent event=mock(OutboxEvent.class);
        when(event.getEventType()).thenReturn("unrelated.event");
        when(outbox.findUnpublished()).thenReturn(List.of(event));
        publisher.publish();
        verifyNoInteractions(kafka,json);
        verify(outbox,never()).markPublished(any());
    }

    @Test void malformedAcceptedEventFailsWithoutMarkingPublished() throws Exception {
        OutboxEvent event=mock(OutboxEvent.class);
        when(event.getEventType()).thenReturn("order.accepted");
        when(event.getPayload()).thenReturn("not-json");
        when(outbox.findUnpublished()).thenReturn(List.of(event));
        when(json.readValue("not-json",OrderAcceptedMessage.class))
                .thenThrow(new IllegalArgumentException("Invalid payload"));
        assertThrows(IllegalStateException.class,()->publisher.publish());
        verify(outbox,never()).markPublished(any());
        verifyNoInteractions(kafka);
    }
}
