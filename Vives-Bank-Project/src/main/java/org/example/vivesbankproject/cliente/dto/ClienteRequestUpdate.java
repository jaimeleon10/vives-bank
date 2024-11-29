package org.example.vivesbankproject.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class ClienteRequestUpdate {
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacio")
    private String apellidos;

    @NotBlank(message = "La calle no puede estar vacia")
    private String calle;

    @NotBlank(message = "El número no puede estar vacio")
    private String numero;

    @NotBlank(message = "El código postal no puede estar vacio")
    @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 numeros")
    private String codigoPostal;

    @NotBlank(message = "El piso no puede estar vacio")
    private String piso;

    @NotBlank(message = "La letra no puede estar vacia")
    private String letra;

    @NotBlank(message = "El email no puede estar vacio")
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser valido")
    private String email;

    @NotBlank(message = "El telefono no puede estar vacio")
    @Pattern(regexp = "^\\d{9}$", message = "El telefono debe tener 9 numeros")
    private String telefono;

    @NotBlank(message = "La foto de perfil no puede estar vacia")
    private String fotoPerfil;

    @NotBlank(message = "La foto del DNI no puede estar vacia")
    private String fotoDni;

    private String userId;
}
