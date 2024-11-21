package org.example.vivesbankproject.cliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.User;
import java.util.Set;
import java.util.UUID;

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
    @Builder.Default
    private Set<Cuenta> cuentas = Set.of();
    private User user;
    private ObjectId idMovimientos;
}