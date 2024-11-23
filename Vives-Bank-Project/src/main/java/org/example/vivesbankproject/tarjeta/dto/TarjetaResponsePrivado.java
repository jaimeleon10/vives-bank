package org.example.vivesbankproject.tarjeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponsePrivado {
    private String guid;
    private Integer cvv;
    private String pin;
}
