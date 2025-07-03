package com.mueblessas.stats.rabbitmq;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.model.statistics.gateways.StatisticsPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StatisticsEventPublisherAdapter implements StatisticsPublisher {

    private final AmqpTemplate amqpTemplate;

    @Override
    public Mono<Void> publish(Statistics statistics) {
        return Mono.fromRunnable(() ->
                amqpTemplate.convertAndSend("event.stats.validated", statistics));
    }
}
