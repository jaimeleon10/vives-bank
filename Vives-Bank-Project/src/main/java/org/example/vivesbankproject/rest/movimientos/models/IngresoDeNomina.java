package org.example.vivesbankproject.rest.movimientos.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Representa un registro de ingreso de nómina en el sistema financiero.
 * Contiene detalles relevantes para identificar los movimientos de nómina entre empresas y empleados.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Clase para representar un ingreso de nómina en el sistema financiero")
public class IngresoDeNomina {

    /**
     * IBAN de destino donde se realizará el ingreso de la nómina (empleado).
     */
    @NotBlank
    @Schema(description = "IBAN de destino donde se realizará el ingreso de la nómina", example = "ES9121000000410200051332")
    private String iban_Destino; // IBAN del destinatario de la nómina

    /**
     * IBAN de origen de la empresa que ejecuta el pago de la nómina.
     */
    @NotBlank
    @Schema(description = "IBAN de origen de la empresa que ejecuta el pago de la nómina", example = "ES9121000000410200051333")
    private String iban_Origen; // IBAN de origen de la empresa

    /**
     * Cantidad de dinero a ingresar como nómina. Validación para asegurar que se encuentra entre 1 y 10,000.
     */
    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    @Schema(description = "Cantidad que se ingresará como nómina", example = "1500.00")
    private Double cantidad;

    /**
     * Nombre de la empresa responsable de realizar el ingreso de nómina.
     */
    @Size(max = 100, message = "El nombre de la empresa no puede tener más de 100 caracteres")
    @NotBlank
    @Schema(description = "Nombre de la empresa que realiza el pago de la nómina", example = "Empresa Ejemplo S.L.")
    private String nombreEmpresa;

    /**
     * CIF de la empresa que realiza el pago. Debe seguir el formato: 9 caracteres alfanuméricos.
     */
    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "El CIF debe tener 9 caracteres alfanuméricos")
    @NotBlank
    @Schema(description = "CIF de la empresa que realiza el pago de nómina", example = "A12345678")
    private String cifEmpresa;
}