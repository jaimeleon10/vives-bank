package org.example.vivesbankproject.movimientoTransaccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.movimientos.dto.TransaccionResponse;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoTransaccionResponse {
    private String guid;
    private String idUsuario;
    private ClienteResponse clienteResponse;
    private TransaccionResponse transacciones;
    private Boolean isDeleted;
}
