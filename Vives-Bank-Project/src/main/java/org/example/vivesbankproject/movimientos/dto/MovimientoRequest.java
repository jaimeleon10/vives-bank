package org.example.vivesbankproject.movimientos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.movimientos.models.Transacciones;
import org.example.vivesbankproject.utils.IdGenerator;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequest {

    @Builder.Default
    String guid = IdGenerator.generarId();

    @NotBlank(message = "El campo movimiento no puede estar vacio")
    String idUsuario;

    @NotBlank(message = "El campo cliente no puede estar vacio")
    String idCliente;

    @NotBlank(message = "El campo transacciones no puede estar vacio")
    private List<Transacciones> transacciones;
}