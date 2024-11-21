package org.example.vivesbankproject.tarjeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponseCVV {
    private String guid;
    private String numeroTarjeta;
    private Integer cvv;
}
