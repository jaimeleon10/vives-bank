package org.example.vivesbankproject.rest.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Representa la información de un pago realizado mediante una tarjeta, incluyendo el número de tarjeta, cantidad y detalles
 * del comercio asociado con el pago.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Clase para representar los detalles de un pago con tarjeta")
public class PagoConTarjeta {

    /**
     * Número de tarjeta utilizado para el pago. Debe tener exactamente 16 dígitos.
     */
    @Schema(description = "Número de tarjeta con 16 dígitos para el pago con tarjeta", example = "1234567812345678")
    @Pattern(regexp = "^\\d{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    @NotBlank
    private String numeroTarjeta;

    /**
     * Cantidad que se está intentando pagar con la tarjeta. Debe estar entre 1 y 10,000.
     */
    @Schema(description = "Cantidad a pagar con la tarjeta. Debe estar entre 1 y 10,000", example = "250")
    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private Double cantidad;

    /**
     * Nombre del comercio asociado con el pago. No debe exceder los 100 caracteres.
     */
    @Schema(description = "Nombre del comercio donde se realizó el pago", example = "Supermercado XYZ")
    @Size(max = 100, message = "El nombre del comercio no puede tener más de 100 caracteres")
    @NotBlank
    private String nombreComercio;
}