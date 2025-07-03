package com.mueblessas.stats.rabbitmq;

import com.mueblessas.stats.model.statistics.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpTemplate;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class StatisticsEventPublisherAdapterTest {

    private AmqpTemplate amqpTemplate;
    private StatisticsEventPublisherAdapter publisherAdapter;

    @BeforeEach
    void setUp() {
        amqpTemplate = mock(AmqpTemplate.class);
        publisherAdapter = new StatisticsEventPublisherAdapter(amqpTemplate);
    }

    @Test
    void shouldPublishStatisticsEvent() {
        Statistics statistics = new Statistics(
                250, 25, 10, 100, 100, 7, 8, "hash-value", 123456789L
        );

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

        StepVerifier.create(publisherAdapter.publish(statistics))
                .verifyComplete();

        verify(amqpTemplate, times(1)).convertAndSend(eq("event.stats.validated"), messageCaptor.capture());

        Statistics published = (Statistics) messageCaptor.getValue();
        assertEquals(statistics, published);
    }
}
