package org.example.vivesbankproject.rest.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.users.dto.UserResponse;

import java.util.Set;

/**
 * DTO para representar a un cliente.
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo que representa la información de un cliente")
public class ClienteJson {
    @Schema(description = "Identificador único del cliente", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid;

    @Schema(description = "Documento de identidad del cliente", example = "12345678A")
    private String dni;

    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Schema(description = "Apellidos del cliente", example = "Pérez García")
    private String apellidos;

    @Schema(description = "Dirección del cliente", implementation = Direccion.class)
    private Direccion direccion;

    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@example.com")
    private String email;

    @Schema(description = "Teléfono de contacto del cliente", example = "+34 600 123 456")
    private String telefono;

    @Schema(description = "URL de la foto de perfil del cliente", example = "https://example.com/foto-perfil.jpg")
    private String fotoPerfil;

    @Schema(description = "URL de la imagen del DNI del cliente", example = "https://example.com/foto-dni.jpg")
    private String fotoDni;

    @Schema(description = "Conjunto de cuentas asociadas al cliente")
    private Set<CuentaResponse> cuentas;
    
    @Schema(description = "Tarjetas vinculada a las cuentas del cliente")
    private Set<TarjetaResponse> tarjetas;

    @Schema(description = "Usuario vinculado al cliente", implementation = UserResponse.class)
    private UserResponse usuario;
    
    @Schema(description = "Fecha de creación del registro", example = "2023-01-01T10:00:00Z")
    private String createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2023-02-01T12:00:00Z")
    private String updatedAt;

    @Schema(description = "Indica si el cliente ha sido eliminado", example = "false")
    private Boolean isDeleted;
}