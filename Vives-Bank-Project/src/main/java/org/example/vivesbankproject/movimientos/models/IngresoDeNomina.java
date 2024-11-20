package org.example.vivesbankproject.movimientos.models;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class IngresoDeNomina extends Transacciones {
    @Pattern(
            regexp = "^ES\\d{22}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    @Size(
            min = 24, max = 24,
            message = "El IBAN español debe tener exactamente 24 caracteres"
    )
    private String iban_Destino;

    @Size(max = 100, message = "El nombre de la empresa no puede tener más de 100 caracteres")
    private String nombreEmpresa;

    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "El CIF debe tener 9 caracteres alfanuméricos")
    private String cifEmpresa;
}