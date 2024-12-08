package org.example.vivesbankproject.rest.tarjeta.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;

@Data
@Builder
public class TarjetaRequestSave {
    @NotBlank(message = "El PIN debe ser un numero de 4 digitos")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un numero de 4 digitos")
    private String pin;

    @Positive(message = "El limite diario debe ser un numero positivo")
    private BigDecimal limiteDiario;

    @Positive(message = "El limite semanal debe ser un numero positivo")
    private BigDecimal limiteSemanal;

    @Positive(message = "El limite mensual debe ser un numero positivo")
    private BigDecimal limiteMensual;

    @NotNull(message = "El tipo de tarjeta no puede ser un campo nulo")
    private TipoTarjeta tipoTarjeta;
}

