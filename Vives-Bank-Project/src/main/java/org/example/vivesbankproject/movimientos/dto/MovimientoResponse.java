package org.example.vivesbankproject.movimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {
    private String guid;
    private String idUsuario;
    private ClienteResponse clienteResponse;
    private List<TransaccionResponse> transacciones;
    private Boolean isDeleted;
}