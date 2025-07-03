package com.mueblessas.stats.web.statistics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class StatisticsRouter {

    @Bean
    public RouterFunction<ServerResponse> route(StatisticsHandler handler) {
        return RouterFunctions.route()
                .POST("/stats", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::handle)
                .build();
    }

}
