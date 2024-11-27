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

    @NotBlank(message = "El DNI no puede estar vacio")
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke]$", message = "El DNI debe tener 8 numeros seguidos de una letra")
    private String dni;

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

    @Email(regexp = ".*@.*\\..*", message = "El email debe ser valido")
    @NotBlank(message = "El email no puede estar vacio")
    private String email;

    @Pattern(regexp = "^\\d{9}$", message = "El telefono debe tener 9 numeros")
    @NotBlank(message = "El telefono no puede estar vacio")
    private String telefono;

    // TODO -> MODIFICAR FOTO DE PERFIL Y DNI
    @NotBlank(message = "La foto de perfil no puede estar vacia")
    private String fotoPerfil;

    @NotBlank(message = "La foto del DNI no puede estar vacia")
    private String fotoDni;

    @NotBlank(message = "El id de usuario no puede estar vacio")
    private String userId;

    @NotNull(message = "El campo de borrado logico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}
