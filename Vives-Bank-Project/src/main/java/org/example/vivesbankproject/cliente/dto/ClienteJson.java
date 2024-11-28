package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.models.Direccion;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteJson {
    private String guid;
    private String dni;
    private String nombre;
    private String apellidos;
    private Direccion direccion;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
    private Set<CuentaResponse> cuentas;
    private TarjetaResponse tarjeta;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}