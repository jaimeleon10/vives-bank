package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteForCuentaResponse {
    private String guid;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
}
