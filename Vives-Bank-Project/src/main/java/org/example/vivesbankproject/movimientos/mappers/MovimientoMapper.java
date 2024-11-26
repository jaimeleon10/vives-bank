package org.example.vivesbankproject.movimientos.mappers;

import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.dto.TransaccionResponse;
import org.example.vivesbankproject.movimientos.models.Movimientos;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoMapper {
    public MovimientoResponse toMovimientoResponse(Movimientos movimientos, ClienteResponse clienteResponse, List<Transacciones> transaccionResponse) {
        return MovimientoResponse.builder()
                .guid(movimientos.getGuid())
                .idUsuario(movimientos.getIdUsuario())
                .clienteResponse(clienteResponse)
                .transacciones(transaccionResponse)
                .isDeleted(movimientos.getIsDeleted())
                .build();
    }

    public Movimientos toMovimientos(Cliente cliente, List<Transacciones> transacciones) {
        return Movimientos.builder()
                .cliente(cliente)
                .transacciones(transacciones)
                .build();
    }
}
