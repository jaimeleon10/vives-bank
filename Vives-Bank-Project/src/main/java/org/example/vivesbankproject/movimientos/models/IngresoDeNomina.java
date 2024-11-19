package org.example.vivesbankproject.movimientos.models;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class IngresoDeNomina extends Transacciones{
    @Pattern(
            regexp = "^ES\\d{22}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    @Size(
            min = 24, max = 24,
            message = "El IBAN español debe tener exactamente 24 caracteres"
    )
    private String iban_Destino;
}
