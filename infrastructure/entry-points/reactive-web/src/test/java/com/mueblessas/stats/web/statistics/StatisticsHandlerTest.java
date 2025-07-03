package com.mueblessas.stats.web.statistics;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.usecase.processstatistics.ProcessStatisticsUseCase;
import com.mueblessas.stats.web.statistics.dto.StatisticsRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsHandlerTest {

    private ProcessStatisticsUseCase useCase;
    private Validator validator;
    private StatisticsHandler handler;

    @BeforeEach
    void setUp() {
        useCase = mock(ProcessStatisticsUseCase.class);
        validator = mock(Validator.class);
        handler = new StatisticsHandler(useCase, validator);
    }

    private StatisticsRequest buildValidRequest() {
        StatisticsRequest request = new StatisticsRequest();
        request.setTotalContactoClientes(250);
        request.setMotivoReclamo(25);
        request.setMotivoGarantia(10);
        request.setMotivoDuda(100);
        request.setMotivoCompra(100);
        request.setMotivoFelicitaciones(7);
        request.setMotivoCambio(8);
        request.setHash("5484062a4be1ce5645eb414663e14f59");
        return request;
    }

    @Test
    void handle_ValidRequest_ReturnsOkResponse() {
        StatisticsRequest requestDto = buildValidRequest();
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(StatisticsRequest.class)).thenReturn(Mono.just(requestDto));
        when(validator.validate(any(StatisticsRequest.class))).thenReturn(Collections.emptySet());

        Statistics stats = Statistics.builder()
                .totalContactoClientes(requestDto.getTotalContactoClientes())
                .motivoReclamo(requestDto.getMotivoReclamo())
                .motivoGarantia(requestDto.getMotivoGarantia())
                .motivoDuda(requestDto.getMotivoDuda())
                .motivoCompra(requestDto.getMotivoCompra())
                .motivoFelicitaciones(requestDto.getMotivoFelicitaciones())
                .motivoCambio(requestDto.getMotivoCambio())
                .hash(requestDto.getHash())
                .build();

        when(useCase.execute(any(Statistics.class))).thenReturn(Mono.just(stats));

        Mono<ServerResponse> responseMono = handler.handle(request);

        ServerResponse response = responseMono.block();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
    }

    @Test
    void handle_InvalidRequest_ShouldReturnBadRequestDueToValidation() {
        StatisticsRequest invalidRequest = buildValidRequest();
        ConstraintViolation<StatisticsRequest> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("El campo no puede ser nulo.");
        Set<ConstraintViolation<StatisticsRequest>> violations = Set.of(violation);

        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(StatisticsRequest.class)).thenReturn(Mono.just(invalidRequest));
        when(validator.validate(any(StatisticsRequest.class))).thenReturn(violations);

        Mono<ServerResponse> responseMono = handler.handle(request);

        ServerResponse response = responseMono.block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }

    @Test
    void handle_InvalidHash_ShouldReturnBadRequest() {
        StatisticsRequest requestDto = buildValidRequest();
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(StatisticsRequest.class)).thenReturn(Mono.just(requestDto));
        when(validator.validate(any(StatisticsRequest.class))).thenReturn(Collections.emptySet());

        when(useCase.execute(any(Statistics.class))).thenReturn(Mono.error(new IllegalArgumentException("Hash invalido")));

        Mono<ServerResponse> responseMono = handler.handle(request);
        
        ServerResponse response = responseMono.block();
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
    }
}
