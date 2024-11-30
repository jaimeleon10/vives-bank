package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cliente.models.Direccion;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.users.models.User;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteJsonAdmin {
    private Long id;
    private String guid;
    private String dni;
    private String nombre;
    private String apellidos;
    private Direccion direccion;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String fotoDni;
    private Set<Cuenta> cuentas;
    private Tarjeta tarjeta;
    private User user;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}