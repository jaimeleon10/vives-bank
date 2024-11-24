package org.example.vivesbankproject.tarjeta.dto;

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
    @NotBlank(message = "El PIN no puede estar vacío")
    @Pattern(regexp = "^[0-9]{4}$", message = "El PIN debe ser un número de 3 dígitos")
    private String pin;

    @Positive(message = "El límite diario debe ser un número positivo")
    private BigDecimal limiteDiario;

    @Positive(message = "El límite semanal debe ser un número positivo")
    private BigDecimal limiteSemanal;

    @Positive(message = "El límite mensual debe ser un número positivo")
    private BigDecimal limiteMensual;

    @NotNull(message = "El campo de borrado lógico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}
