package org.example.vivesbankproject.movimientoTransaccion.mappers;

import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientoTransaccion.dto.MovimientoTransaccionResponse;
import org.example.vivesbankproject.movimientoTransaccion.models.MovimientoTransaccion;
import org.example.vivesbankproject.movimientos.dto.TransaccionResponse;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.springframework.stereotype.Component;

@Component
public class MovimientoTransaccionMapper {
    public MovimientoTransaccionResponse toMovimientoTransaccionResponse(MovimientoTransaccion movimientoTransaccion, ClienteResponse clienteResponse, TransaccionResponse transaccionResponse) {
        return MovimientoTransaccionResponse.builder()
                .guid(movimientoTransaccion.getGuid())
                .idUsuario(movimientoTransaccion.getIdUsuario())
                .clienteResponse(clienteResponse)
                .transacciones(transaccionResponse)
                .isDeleted(movimientoTransaccion.getIsDeleted())
                .build();
    }

    public MovimientoTransaccion toMovimientoTransaccion(Cliente cliente, Transacciones transacciones) {
        return MovimientoTransaccion.builder()
                .cliente(cliente)
                .transacciones(transacciones)
                .build();
    }
}