package org.example.vivesbankproject.movimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionResponse {
    private String guid;
    private LocalDateTime fecha;
    private Double cantidad;
    private String concepto;
}
