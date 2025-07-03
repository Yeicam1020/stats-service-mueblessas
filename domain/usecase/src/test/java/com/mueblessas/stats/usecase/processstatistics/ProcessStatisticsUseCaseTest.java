package com.mueblessas.stats.usecase.processstatistics;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.model.statistics.gateways.StatisticsPublisher;
import com.mueblessas.stats.model.statistics.gateways.StatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessStatisticsUseCaseTest {

    private StatisticsRepository repository;
    private StatisticsPublisher publisher;
    private ProcessStatisticsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(StatisticsRepository.class);
        publisher = mock(StatisticsPublisher.class);
        useCase = new ProcessStatisticsUseCase(repository, publisher);
    }

    @Test
    void shouldProcessStatisticsWhenHashIsValid() {
        Statistics stats = getValidStats();
        stats.setHash("5484062a4be1ce5645eb414663e14f59"); // Hash correcto para la cadena: 250,25,10,100,100,7,8

        when(repository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(publisher.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(stats))
                .expectNextMatches(result -> result.getTimestamp() > 0)
                .verifyComplete();

        verify(repository).save(any());
        verify(publisher).publish(any());
    }

    @Test
    void shouldFailWhenHashIsInvalid() {
        Statistics stats = getValidStats();
        stats.setHash("invalidhash");

        StepVerifier.create(useCase.execute(stats))
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().equals("Hash invalido"))
                .verify();

        verify(repository, never()).save(any());
        verify(publisher, never()).publish(any());
    }

    private Statistics getValidStats() {
        return Statistics.builder()
                .totalContactoClientes(250)
                .motivoReclamo(25)
                .motivoGarantia(10)
                .motivoDuda(100)
                .motivoCompra(100)
                .motivoFelicitaciones(7)
                .motivoCambio(8)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
