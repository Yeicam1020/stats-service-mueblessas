package com.mueblessas.stats.dynamodb;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.model.statistics.gateways.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;

@Repository
@RequiredArgsConstructor
public class StatisticsRepositoryAdapter implements StatisticsRepository {

    private final DynamoDbAsyncTable<StatisticsDynamoEntity> table;
    @Override
    public Mono<Statistics> save(Statistics statistics) {
        StatisticsDynamoEntity entity = StatisticsDynamoEntity.fromDomain(statistics);
        return Mono.fromFuture(() -> table.putItem(entity)).thenReturn(statistics);
    }
}
