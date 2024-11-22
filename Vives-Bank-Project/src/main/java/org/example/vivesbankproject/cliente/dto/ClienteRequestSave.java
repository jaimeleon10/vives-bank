package org.example.vivesbankproject.cliente.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.users.models.User;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestSave {

    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke]$", message = "El DNI debe tener 8 números seguidos de una letra")
    private String dni;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacío")
    private String apellidos;

    @Email(regexp = ".*@.*\\..*", message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener 9 números")
    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @NotBlank(message = "La foto de perfil no puede estar vacía")
    private String fotoPerfil;

    @NotBlank(message = "La foto del DNI no puede estar vacía")
    private String fotoDni;

    @Builder.Default
    private Set<Cuenta> cuentas = new HashSet<>();

    @NotNull(message = "El usuario no puede ser un campo nulo")
    private User user;
}
