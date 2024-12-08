package org.example.vivesbankproject.rest.tarjeta.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class TarjetaResponse implements Serializable {
    private String guid;
    private String numeroTarjeta;
    private String fechaCaducidad;
    private String limiteDiario;
    private String limiteSemanal;
    private String limiteMensual;
    private TipoTarjeta tipoTarjeta;
    private String createdAt;
    private String updatedAt;
    public Boolean isDeleted;
}
