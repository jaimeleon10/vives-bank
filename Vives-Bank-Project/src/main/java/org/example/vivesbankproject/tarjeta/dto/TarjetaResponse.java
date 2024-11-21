package org.example.vivesbankproject.tarjeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponse {
    private String guid;
    private String numeroTarjeta;
    private LocalDate fechaCaducidad;
    private BigDecimal limiteDiario;
    private BigDecimal limiteSemanal;
    private BigDecimal limiteMensual;
    private TipoTarjeta tipoTarjeta;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Boolean isDeleted;
}
