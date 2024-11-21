package org.example.vivesbankproject.tarjeta.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TarjetaRequest {

    @NotBlank(message = "El número de tarjeta no puede estar vacío")
    private String numeroTarjeta;

    @Future(message = "La fecha de caducidad debe estar en el futuro")
    private LocalDate fechaCaducidad;

    @NotNull(message = "El CVV no puede estar vacío")
    @Min(value = 100, message = "El CVV debe ser un número de tres dígitos")
    @Max(value = 999, message = "El CVV debe ser un número de tres dígitos")
    private Integer cvv;

    @NotBlank(message = "El PIN no puede estar vacío")
    private String pin;

    @Positive(message = "El límite diario debe ser un número positivo")
    private BigDecimal limiteDiario;

    @Positive(message = "El límite semanal debe ser un número positivo")
    private BigDecimal limiteSemanal;

    @Positive(message = "El límite mensual debe ser un número positivo")
    private BigDecimal limiteMensual;

    @NotNull(message = "El ID del tipo de tarjeta no puede estar vacío")
    private TipoTarjeta tipoTarjeta;
}
