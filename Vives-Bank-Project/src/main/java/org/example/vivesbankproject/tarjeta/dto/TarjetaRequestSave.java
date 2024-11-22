package org.example.vivesbankproject.tarjeta.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TarjetaRequestSave {
    //@NotBlank(message = "El PIN no puede estar vacío")
    //@Pattern(regexp = "^[0-9]{3}$", message = "El PIN debe ser un número de 3 dígitos")
    private String pin;

  //  @Positive(message = "El límite diario debe ser un número positivo")
    private BigDecimal limiteDiario;

   // @Positive(message = "El límite semanal debe ser un número positivo")
    private BigDecimal limiteSemanal;

  //  @Positive(message = "El límite mensual debe ser un número positivo")
    private BigDecimal limiteMensual;

  //  @NotBlank(message = "El tipo de tarjeta no puede ser un campo vacío")
    private TipoTarjeta tipoTarjeta;
}
