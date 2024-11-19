package org.example.vivesbankproject.tarjeta.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
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
    private Double limiteDiario;

    @Positive(message = "El límite semanal debe ser un número positivo")
    private Double limiteSemanal;

    @Positive(message = "El límite mensual debe ser un número positivo")
    private Double limiteMensual;

    @NotNull(message = "El ID del tipo de tarjeta no puede estar vacío")
    private String tipoTarjeta;

    @NotNull(message = "El ID de la cuenta no puede estar vacío")
    private UUID cuentaId;
}
