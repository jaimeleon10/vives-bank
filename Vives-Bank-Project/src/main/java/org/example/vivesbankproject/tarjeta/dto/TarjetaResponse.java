package org.example.vivesbankproject.tarjeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponse {
    private UUID id;
    private String numeroTarjeta;
    private LocalDate fechaCaducidad;
    private Integer cvv;
    private BigDecimal limiteDiario;
    private BigDecimal limiteSemanal;
    private BigDecimal limiteMensual;
    private String tipoTarjeta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
