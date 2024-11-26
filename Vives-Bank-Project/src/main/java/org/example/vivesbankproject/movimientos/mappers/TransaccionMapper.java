package org.example.vivesbankproject.movimientos.mappers;

import org.example.vivesbankproject.movimientos.dto.TransaccionResponse;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransaccionMapper {
    public TransaccionResponse toTransaccionResponse(Transacciones transacciones) {
        return TransaccionResponse.builder()
                .guid(transacciones.getGuid())
                .fecha(transacciones.getFecha_transaccion())
                .cantidad(transacciones.getCantidad())
                .concepto(transacciones.getConcepto())
                .build();
    }

    public List<TransaccionResponse> toTransaccionResponseList(List<Transacciones> transacciones) {
        return transacciones.stream()
                .map(this::toTransaccionResponse)
                .collect(Collectors.toList());
    }
}