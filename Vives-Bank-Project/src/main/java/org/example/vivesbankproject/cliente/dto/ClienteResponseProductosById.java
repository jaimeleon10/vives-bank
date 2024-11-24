package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseProductosById {
    private String guid;
    private String nombre;
    private CuentaResponse cuenta;
    private TarjetaResponse tarjeta;
}
