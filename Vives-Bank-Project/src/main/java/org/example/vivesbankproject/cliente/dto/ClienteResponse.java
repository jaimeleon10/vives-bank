package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.users.dto.UserResponse;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private String guid;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
    private Set<CuentaResponse> cuentas;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}