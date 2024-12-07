package org.example.vivesbankproject.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * Clase que representa la solicitud para actualizar la información de un cliente existente en el sistema.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestUpdate {

    /**
     * Nombre del cliente para la actualización de sus datos.
     * @param nombre Nombre del cliente. No puede estar vacío.
     */
    @Schema(description = "El nombre del cliente", example = "Juan")
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    /**
     * Apellidos del cliente para la actualización de sus datos.
     * @param apellidos Apellidos que deben ser especificados. No puede estar vacío.
     */
    @Schema(description = "Los apellidos del cliente", example = "Pérez García")
    @NotBlank(message = "Los apellidos no pueden estar vacio")
    private String apellidos;

    /**
     * Calle de la dirección del cliente para la actualización.
     * @param calle Dirección calle donde reside el cliente. No puede estar vacía.
     */
    @Schema(description = "La calle de la dirección del cliente", example = "Calle Falsa")
    @NotBlank(message = "La calle no puede estar vacia")
    private String calle;

    /**
     * Número en la dirección del cliente para la actualización.
     * @param numero El número de la dirección. Campo obligatorio.
     */
    @Schema(description = "El número de la dirección del cliente", example = "10")
    @NotBlank(message = "El numero no puede estar vacio")
    private String numero;

    /**
     * Código postal para validar la dirección del cliente.
     * @param codigoPostal El código postal que debe tener exactamente 5 dígitos.
     */
    @Schema(description = "El código postal de la dirección", example = "28001")
    @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 numeros")
    @NotNull(message = "El codigo postal no puede ser nulo")
    private String codigoPostal;

    /**
     * Piso en la dirección para la actualización de datos.
     * @param piso Piso en la dirección. Campo obligatorio.
     */
    @Schema(description = "El piso en la dirección del cliente", example = "2")
    @NotBlank(message = "El piso no puede estar vacio")
    private String piso;

    /**
     * Letra en la dirección para identificar correctamente la vivienda.
     * @param letra La letra que complementa la dirección. Campo obligatorio.
     */
    @Schema(description = "La letra de la dirección del cliente", example = "B")
    @NotBlank(message = "La letra no puede estar vacia")
    private String letra;

    /**
     * Correo electrónico para identificar al cliente.
     * @param email El correo electrónico válido del cliente.
     */
    @Schema(description = "El correo electrónico del cliente", example = "cliente@example.com")
    @Email(regexp = ".*@.*\\..*", message = "El email debe ser valido")
    @NotNull(message = "El email no puede ser un campo nulo")
    private String email;

    /**
     * Número de teléfono para el contacto con el cliente.
     * @param telefono El número de teléfono debe tener exactamente 9 números.
     */
    @Schema(description = "El número de teléfono del cliente", example = "612345678")
    @Pattern(regexp = "^\\d{9}$", message = "El telefono debe tener 9 numeros")
    @NotNull(message = "El telefono no puede ser nulo")
    private String telefono;

    /**
     * Foto de perfil del cliente como parte de su información actualizada.
     * @param fotoPerfil URL que contiene la foto de perfil.
     */
    @Schema(description = "URL de la foto de perfil del cliente", example = "https://example.com/foto-perfil.jpg")
    @NotBlank(message = "La foto de perfil no puede estar vacia")
    private String fotoPerfil;

    /**
     * Foto del DNI del cliente como parte de su información actualizada.
     * @param fotoDni URL que contiene la foto del DNI del cliente.
     */
    @Schema(description = "URL de la foto del DNI del cliente", example = "https://example.com/foto-dni.jpg")
    @NotBlank(message = "La foto del DNI no puede estar vacia")
    private String fotoDni;

    /**
     * Identificador de usuario asociado con el cliente para realizar la actualización.
     * @param userId El identificador único del usuario.
     */
    @Schema(description = "El identificador del usuario asociado al cliente", example = "user123")
    @NotBlank(message = "El id de usuario no puede estar vacio")
    private String userId;
}
