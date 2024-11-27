package org.example.vivesbankproject.cuenta.dto.tipoCuenta;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class TipoCuentaResponse implements Serializable {
    private String guid;
    private String nombre;
    private String interes;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}
