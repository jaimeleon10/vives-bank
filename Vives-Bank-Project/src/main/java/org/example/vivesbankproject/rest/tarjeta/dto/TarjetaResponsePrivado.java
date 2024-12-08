package org.example.vivesbankproject.rest.tarjeta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponsePrivado implements Serializable {
    private String guid;
    private String cvv;
    private String pin;
}
