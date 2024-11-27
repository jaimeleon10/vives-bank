package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transferencia {
    @NotBlank
    @Pattern(
            regexp = "^ES\\d{22}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    @Size(
            min = 24, max = 24,
            message = "El IBAN español debe tener exactamente 24 caracteres"
    )
    private String iban_Origen;

    @NotBlank
    @Pattern(
            regexp = "^ES\\d{22}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    @Size(
            min = 24, max = 24,
            message = "El IBAN español debe tener exactamente 24 caracteres"
    )
    private String iban_Destino;

    @NotBlank
    @Size(max = 100, message = "El nombre del beneficiario no puede tener más de 100 caracteres")
    private String nombreBeneficiario;
}