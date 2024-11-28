package org.example.vivesbankproject.movimientos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.movimientos.models.Transferencia;
import org.example.vivesbankproject.utils.generators.IdGenerator;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimientoRequest {

    @Builder.Default
    private String guid = IdGenerator.generarId();

    private String clienteGuid;

    private Domiciliacion domiciliacion;

    private IngresoDeNomina ingresoDeNomina;

    private PagoConTarjeta pagoConTarjeta;

    private Transferencia transferencia;

}

