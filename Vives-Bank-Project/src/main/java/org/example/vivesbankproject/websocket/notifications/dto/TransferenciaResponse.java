package org.example.vivesbankproject.websocket.notifications.dto;

import java.math.BigDecimal;

public record TransferenciaResponse(
        String ibanOrigen,
        String ibanDestino,
        BigDecimal cantidad,
        String nombreBeneficiario
) {}
