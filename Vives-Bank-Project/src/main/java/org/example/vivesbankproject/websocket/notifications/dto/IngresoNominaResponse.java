package org.example.vivesbankproject.websocket.notifications.dto;


public record IngresoNominaResponse (
    String ibanOrigen,
    String ibanDestino,
    Double cantidad,
    String nombreEmpresa,
    String cifEmpresa
) {}
