package org.example.vivesbankproject.movimientos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequest {
    @NotBlank(message = "El campo movimiento no puede estar vacio")
    String idUsuario;

    @NotBlank(message = "El campo cliente no puede estar vacio")
    String idCliente;

    @NotBlank(message = "El campo transacciones no puede estar vacio")
    List<String> transaccionesIds;
}