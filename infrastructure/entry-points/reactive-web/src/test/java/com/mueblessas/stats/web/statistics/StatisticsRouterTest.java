package com.mueblessas.stats.web.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.mockito.ArgumentMatchers.any;

class StatisticsRouterTest {

    private WebTestClient webTestClient;
    private StatisticsHandler mockHandler;

    @BeforeEach
    void setUp() {
        mockHandler = Mockito.mock(StatisticsHandler.class);

        Mockito.when(mockHandler.handle(any())).thenReturn(ServerResponse.ok().bodyValue("Mock OK"));

        RouterFunction<ServerResponse> routerFunction = new StatisticsRouter().route(mockHandler);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testRouteToHandle() {
        webTestClient.post()
                .uri("/stats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Mock OK");
    }
}
