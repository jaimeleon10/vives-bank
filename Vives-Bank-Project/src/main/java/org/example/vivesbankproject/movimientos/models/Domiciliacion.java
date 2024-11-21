package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonTypeName("DOMICILIACION")
@AllArgsConstructor
@NoArgsConstructor
public class Domiciliacion extends Transacciones {
    @Pattern(
            regexp = "^ES\\d{22}$",
            message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos"
    )
    @Size(
            min = 24, max = 24,
            message = "El IBAN español debe tener exactamente 24 caracteres"
    )
    private String iban_Origen;

    @Size(max = 100, message = "El nombre del acreedor no puede tener más de 100 caracteres")
    private String nombreAcreedor;

    @Pattern(regexp = "^[A-Z0-9]{8}$", message = "El identificador del acreedor debe tener 8 caracteres alfanuméricos")
    private String identificadorAcreedor;

}