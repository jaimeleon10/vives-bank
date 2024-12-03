package org.example.vivesbankproject.movimientos.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngresoDeNomina  {

   // @Pattern(regexp = "^ES\\d{22}$",message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos")
 //   @Size(min = 24, max = 24,message = "El IBAN español debe tener exactamente 24 caracteres")
    @NotBlank
    private String iban_Destino; //iban de la persona que recibe el ingreso de la nomina

//    @Pattern(regexp = "^ES\\d{22}$",message = "El IBAN español debe comenzar con 'ES' seguido de 22 dígitos")
  //  @Size(min = 24, max = 24,message = "El IBAN español debe tener exactamente 24 caracteres")
    @NotBlank
    private String iban_Origen; //iban de la empresa

    @Min(value = 1, message = "La cantidad debe ser mayor a 1")
    @Max(value = 10000, message = "La cantidad debe ser menor a 10000")
    private Double cantidad;

    @Size(max = 100, message = "El nombre de la empresa no puede tener más de 100 caracteres")
    @NotBlank
    private String nombreEmpresa;

    @Pattern(regexp = "^[A-Z0-9]{9}$", message = "El CIF debe tener 9 caracteres alfanuméricos")
    @NotBlank
    private String cifEmpresa;

}