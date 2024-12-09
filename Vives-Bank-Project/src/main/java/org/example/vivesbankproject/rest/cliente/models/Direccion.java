package org.example.vivesbankproject.rest.cliente.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa una dirección de cliente en la base de datos.
 * Contiene la información de ubicación como calle, número, código postal, piso y letra.
 * Se utiliza como parte embebida en la entidad {@link Cliente}.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {

    /**
     * Nombre de la calle de la dirección del cliente.
     *
     * @param calle Nombre de la calle.
     */
    @NotBlank(message = "La calle no puede estar vacia")
    @Schema(description = "Nombre de la calle de la dirección del cliente", example = "Calle Falsa")
    private String calle;

    /**
     * Número de la dirección.
     *
     * @param numero Número de la dirección.
     */
    @NotBlank(message = "El número no puede estar vacio")
    @Schema(description = "Número de la dirección del cliente", example = "123")
    private String numero;

    /**
     * Código postal de la dirección.
     * Debe contener exactamente 5 números.
     *
     * @param codigoPostal Código postal en formato de 5 dígitos.
     */
    @NotBlank(message = "El código postal no puede estar vacío")
    @Pattern(regexp = "^\\d{5}$", message = "El código postal debe tener 5 números")
    @Schema(description = "Código postal de la dirección. Debe contener exactamente 5 números", example = "28001")
    private String codigoPostal;

    /**
     * Piso de la dirección.
     *
     * @param piso Piso de la dirección.
     */
    @NotBlank(message = "El piso no puede estar vacio")
    @Schema(description = "Piso de la dirección del cliente", example = "3")
    private String piso;

    /**
     * Letra de la dirección asociada al piso.
     *
     * @param letra Letra de la dirección.
     */
    @NotBlank(message = "La letra no puede estar vacia")
    @Schema(description = "Letra de la dirección asociada al piso", example = "B")
    private String letra;
}
