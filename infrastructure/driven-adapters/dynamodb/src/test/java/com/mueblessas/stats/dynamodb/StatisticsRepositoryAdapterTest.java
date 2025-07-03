package com.mueblessas.stats.dynamodb;

import com.mueblessas.stats.model.statistics.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StatisticsRepositoryAdapterTest {

    private DynamoDbAsyncTable<StatisticsDynamoEntity> mockTable;
    private StatisticsRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        mockTable = mock(DynamoDbAsyncTable.class);
        repositoryAdapter = new StatisticsRepositoryAdapter(mockTable);
    }

    @Test
    void shouldSaveStatistics() {
        Statistics statistics = Statistics.builder()
                .totalContactoClientes(250)
                .motivoReclamo(25)
                .motivoGarantia(10)
                .motivoDuda(100)
                .motivoCompra(100)
                .motivoFelicitaciones(7)
                .motivoCambio(8)
                .hash("hash")
                .timestamp(System.currentTimeMillis())
                .build();

        when(mockTable.putItem(any(StatisticsDynamoEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        Mono<Statistics> result = repositoryAdapter.save(statistics);

        StepVerifier.create(result)
                .expectNext(statistics)
                .verifyComplete();

        verify(mockTable, times(1)).putItem(any(StatisticsDynamoEntity.class));
    }
}
