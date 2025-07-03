package com.mueblessas.stats.model.statistics.gateways;

import com.mueblessas.stats.model.statistics.Statistics;
import reactor.core.publisher.Mono;

public interface StatisticsRepository {
    Mono<Statistics> save(Statistics statistics);
}
