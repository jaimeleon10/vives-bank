package org.example.vivesbankproject.websocket.notifications.dto;

public record PagoConTarjetaResponse(
        String numeroTarjeta,
        Double cantidad,
        String nombreComercio
) {}
