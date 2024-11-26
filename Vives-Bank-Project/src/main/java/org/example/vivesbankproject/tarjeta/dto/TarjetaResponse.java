package org.example.vivesbankproject.tarjeta.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class TarjetaResponse implements Serializable {
    private String guid;
    private String numeroTarjeta;
    private String fechaCaducidad;
    private String limiteDiario;
    private String limiteSemanal;
    private String limiteMensual;
    private TipoTarjeta tipoTarjeta;
    private String createdAt;
    private String updatedAt;
    public Boolean isDeleted;
}
