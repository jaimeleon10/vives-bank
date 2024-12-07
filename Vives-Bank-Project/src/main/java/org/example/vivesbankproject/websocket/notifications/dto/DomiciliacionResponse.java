package org.example.vivesbankproject.websocket.notifications.dto;

import java.math.BigDecimal;

public record DomiciliacionResponse(
    String guid,
    String ibanOrigen,
    String ibanDestino,
    BigDecimal cantidad,
    String nombreAcreedor,
    String fechaInicio,
    String periodicidad,
    boolean activa,
    String ultimaEjecucion
    ){}
