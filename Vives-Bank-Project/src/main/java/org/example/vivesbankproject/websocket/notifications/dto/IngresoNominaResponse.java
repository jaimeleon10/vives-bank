package org.example.vivesbankproject.websocket.notifications.dto;


public record IngresoNominaResponse (
    String ibanDestino,
    Double cantidad,
    String nombreEmpresa,
    String cifEmpresa
){}
