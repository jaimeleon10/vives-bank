package org.example.vivesbankproject.rest.tarjeta.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TarjetaRequestUpdate {
    @NotBlank(message = "El PIN no puede estar vacio")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un numero de 4 digitos")
    private String pin;

    @Positive(message = "El limite diario debe ser un numero positivo")
    private BigDecimal limiteDiario;

    @Positive(message = "El limite semanal debe ser un numero positivo")
    private BigDecimal limiteSemanal;

    @Positive(message = "El limite mensual debe ser un numero positivo")
    private BigDecimal limiteMensual;

    @NotNull(message = "El campo de borrado logico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}

