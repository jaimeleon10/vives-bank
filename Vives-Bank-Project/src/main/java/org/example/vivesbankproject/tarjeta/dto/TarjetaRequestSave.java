package org.example.vivesbankproject.tarjeta.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;

@Data
@Builder
public class TarjetaRequestSave {
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un número de 3 dígitos")
    private String pin;

    @Positive(message = "El límite diario debe ser un número positivo")
    private BigDecimal limiteDiario;

    @Positive(message = "El límite semanal debe ser un número positivo")
    private BigDecimal limiteSemanal;

    @Positive(message = "El límite mensual debe ser un número positivo")
    private BigDecimal limiteMensual;

    @NotNull(message = "El tipo de tarjeta no puede ser un campo nulo")
    private TipoTarjeta tipoTarjeta;
}
