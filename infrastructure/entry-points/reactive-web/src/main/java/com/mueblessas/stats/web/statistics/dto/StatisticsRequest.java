package com.mueblessas.stats.web.statistics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class StatisticsRequest {

    @NotNull(message = "El total de contacto con clientes no puede ser nulo.")
    private Integer totalContactoClientes;

    @NotNull(message = "El motivo de reclamo no puede ser nulo.")
    private Integer motivoReclamo;

    @NotNull(message = "El motivo de garantía no puede ser nulo.")
    private Integer motivoGarantia;

    @NotNull(message = "El motivo de duda no puede ser nulo.")
    private Integer motivoDuda;

    @NotNull(message = "El motivo de compra no puede ser nulo.")
    private Integer motivoCompra;

    @NotNull(message = "El motivo de felicitaciones no puede ser nulo.")
    private Integer motivoFelicitaciones;

    @NotNull(message = "El motivo de cambio no puede ser nulo.")
    private Integer motivoCambio;

    @NotNull(message = "El hash no puede ser nulo.")
    private String hash;
}

