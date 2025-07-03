package com.mueblessas.stats.usecase.processstatistics;

import com.mueblessas.stats.model.statistics.Statistics;
import com.mueblessas.stats.model.statistics.gateways.StatisticsPublisher;
import com.mueblessas.stats.model.statistics.gateways.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

@RequiredArgsConstructor
public class ProcessStatisticsUseCase {

    private final StatisticsRepository repository;
    private final StatisticsPublisher publisher;

    public Mono<Statistics> execute(Statistics stats) {
        String expectedHash = generateMD5(stats);

        if (!expectedHash.equalsIgnoreCase(stats.getHash())) {
            return Mono.error(new IllegalArgumentException("Hash invalido"));
        }

        stats.setTimestamp(Instant.now().toEpochMilli());

        return repository.save(stats)
                .flatMap(saved -> publisher.publish(saved).thenReturn(saved));
    }

    public String generateMD5(Statistics stats) {
        try {
            String data = getConcatenatedData(stats);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando hash MD5", e);
        }
    }

    public String getConcatenatedData(Statistics stats) {
        return String.format("%d,%d,%d,%d,%d,%d,%d",
                stats.getTotalContactoClientes(),
                stats.getMotivoReclamo(),
                stats.getMotivoGarantia(),
                stats.getMotivoDuda(),
                stats.getMotivoCompra(),
                stats.getMotivoFelicitaciones(),
                stats.getMotivoCambio());
    }
}
