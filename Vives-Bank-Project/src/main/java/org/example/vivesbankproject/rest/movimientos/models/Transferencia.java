package org.example.vivesbankproject.rest.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transferencia {

    @NotBlank
    private String iban_Origen; // iban de la persona que envía la transferencia

    @NotBlank
    private String iban_Destino; //iban de la persona o entidad que recibe la transferencia

    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private BigDecimal cantidad;

    @NotBlank
    @Size(max = 100, message = "El nombre del beneficiario no puede tener más de 100 caracteres")
    private String nombreBeneficiario;

    private String movimientoDestino;
}