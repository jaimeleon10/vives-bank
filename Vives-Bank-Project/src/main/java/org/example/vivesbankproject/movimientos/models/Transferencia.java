package org.example.vivesbankproject.movimientos.models;

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


   // @Pattern(regexp = "^ES\\d{22}$", message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos")
   // @Size(min = 24, max = 24, message = "El IBAN español debe tener exactamente 24 caracteres")
    @NotBlank
    private String iban_Origen; // iban de la persona que envía la transferencia

    //@Pattern(regexp = "^ES\\d{22}$",message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos")
    //@Size(min = 24, max = 24,message = "El IBAN español debe tener exactamente 24 caracteres")
    @NotBlank
    private String iban_Destino; //iban de la persona o entidad que recibe la transferencia

    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private BigDecimal cantidad;

    @NotBlank
    @Size(max = 100, message = "El nombre del beneficiario no puede tener más de 100 caracteres")
    private String nombreBeneficiario;
}