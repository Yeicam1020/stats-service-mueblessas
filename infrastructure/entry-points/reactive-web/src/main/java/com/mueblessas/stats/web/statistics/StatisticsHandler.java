package com.mueblessas.stats.web.statistics;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.usecase.processstatistics.ProcessStatisticsUseCase;
import com.mueblessas.stats.web.statistics.dto.StatisticsRequest;
import com.mueblessas.stats.web.statistics.dto.StatisticsResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StatisticsHandler {

    private static final Logger log = LoggerFactory.getLogger(StatisticsHandler.class);
    private final ProcessStatisticsUseCase useCase;
    private final Validator validator;

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);

    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.bodyToMono(StatisticsRequest.class)
                .flatMap(this::validate)
                .flatMap(this::validateAndProcess)
                .onErrorResume(IllegalArgumentException.class, this::handleIllegalArgumentError);
    }

    private Mono<StatisticsRequest> validate(StatisticsRequest dto) {
        Set<ConstraintViolation<StatisticsRequest>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errorMsg = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(errorMsg));
        }
        return Mono.just(dto);
    }

    private Mono<ServerResponse> validateAndProcess(StatisticsRequest requestDto) {
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

        log.info("===== VERIFICACION HASH =====");
        log.info("Cadena para generar hash: {}", useCase.getConcatenatedData(stats));
        log.info("Hash esperado (calculado): {}", useCase.generateMD5(stats));
        log.info("Hash recibido (desde request): {}", stats.getHash());
        log.info("================================");

        return useCase.execute(stats)
                .then(ServerResponse.ok()
                        .contentType(APPLICATION_JSON_UTF8)
                        .bodyValue(StatisticsResponse.builder()
                                .code(200)
                                .message("Estadistica procesada correctamente")
                                .data(stats)
                                .build()));
    }

    private Mono<ServerResponse> handleIllegalArgumentError(Throwable ex) {
        log.error("Error procesando estadística: {}", ex.getMessage());
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(APPLICATION_JSON_UTF8)
                .bodyValue(StatisticsResponse.builder()
                        .code(400)
                        .message(ex.getMessage())
                        .data(null)
                        .build());
    }
}
