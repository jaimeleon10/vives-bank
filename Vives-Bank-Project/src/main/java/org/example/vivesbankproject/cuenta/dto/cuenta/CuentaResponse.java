package org.example.vivesbankproject.cuenta.dto.cuenta;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
public class CuentaResponse implements Serializable {
    private String guid;
    private String iban;
    private String saldo;
    private String tipoCuentaId;
    private String tarjetaId;
    private String clienteId;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}