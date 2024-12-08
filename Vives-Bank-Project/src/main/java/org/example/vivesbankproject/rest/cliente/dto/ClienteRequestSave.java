package org.example.vivesbankproject.rest.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la solicitud para guardar un nuevo cliente en el sistema.
 * Contiene la información básica requerida para el registro de un cliente, incluyendo datos personales,
 * dirección, información de contacto, foto de perfil y foto del DNI.
 *
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestSave {

    @Schema(description = "El DNI del cliente. Debe tener 8 números seguidos de una letra válida", example = "12345678A")
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke]$", message = "El DNI debe tener 8 numeros seguidos de una letra")
    @NotNull(message = "El DNI no puede ser nulo")
    private String dni;

    @Schema(description = "El nombre del cliente", example = "Juan")
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @Schema(description = "Los apellidos del cliente", example = "Pérez García")
    @NotBlank(message = "Los apellidos no pueden estar vacio")
    private String apellidos;

    @Schema(description = "La calle de la dirección del cliente", example = "Calle Falsa")
    @NotBlank(message = "La calle no puede estar vacia")
    private String calle;

    @Schema(description = "El número de la dirección del cliente", example = "10")
    @NotBlank(message = "El numero no puede estar vacio")
    private String numero;

    @Schema(description = "El código postal de la dirección", example = "28001")
    @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 numeros")
    @NotNull(message = "El codigo postal no puede ser nulo")
    private String codigoPostal;

    @Schema(description = "El piso del cliente", example = "2")
    @NotBlank(message = "El piso no puede estar vacio")
    private String piso;

    @Schema(description = "La letra del cliente en la dirección", example = "B")
    @NotBlank(message = "La letra no puede estar vacia")
    private String letra;

    @Schema(description = "El correo electrónico del cliente", example = "cliente@example.com")
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser valido")
    @NotNull(message = "El email no puede ser nulo")
    private String email;

    @Schema(description = "El número de teléfono del cliente", example = "612345678")
    @Pattern(regexp = "^\\d{9}$", message = "El telefono debe tener 9 numeros")
    @NotNull(message = "El telefono no puede ser nulo")
    private String telefono;

    @Schema(description = "La URL de la foto de perfil del cliente", example = "https://example.com/foto-perfil.jpg")
    @NotBlank(message = "La foto de perfil no puede estar vacia")
    private String fotoPerfil;

    @Schema(description = "La URL de la foto del DNI del cliente", example = "https://example.com/foto-dni.jpg")
    @NotBlank(message = "La foto del DNI no puede estar vacia")
    private String fotoDni;

    @Schema(description = "El identificador del usuario asociado al cliente", example = "user123")
    @NotBlank(message = "El id de usuario no puede estar vacio")
    private String userId;

    @Schema(description = "Indica si el cliente ha sido marcado como eliminado de forma lógica", example = "false")
    @NotNull(message = "El campo de borrado logico no puede ser nulo")
    @Builder.Default
    private Boolean isDeleted = false;
}