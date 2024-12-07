package org.example.vivesbankproject.cliente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
/**
 * Clase que representa la respuesta para la información del cliente devuelta por el sistema.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de información de respuesta del cliente")
public class ClienteResponse implements Serializable {

    @Schema(description = "GUID único del cliente", example = "123e4567-e89b-12d3-a456-426614174000")
    private String guid;

    @Schema(description = "Documento Nacional de Identidad del cliente", example = "12345678A")
    private String dni;

    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Schema(description = "Apellidos del cliente", example = "Pérez García")
    private String apellidos;

    @Schema(description = "Nombre de la calle en la dirección", example = "Calle Falsa")
    private String calle;

    @Schema(description = "Número de la dirección", example = "10")
    private String numero;

    @Schema(description = "Código Postal de la dirección", example = "28001")
    private String codigoPostal;

    @Schema(description = "Piso en la dirección", example = "2")
    private String piso;

    @Schema(description = "Letra en la dirección para identificar correctamente la vivienda", example = "B")
    private String letra;

    @Schema(description = "Correo electrónico del cliente", example = "cliente@example.com")
    private String email;

    @Schema(description = "Número de teléfono del cliente", example = "+34 612345678")
    private String telefono;

    @Schema(description = "URL de la foto de perfil del cliente", example = "https://example.com/foto-perfil.jpg")
    private String fotoPerfil;

    @Schema(description = "URL de la foto del DNI del cliente", example = "https://example.com/foto-dni.jpg")
    private String fotoDni;

    @Schema(description = "Identificador de usuario relacionado con el cliente", example = "user123")
    private String userId;

    @Schema(description = "Fecha de creación del registro", example = "2023-01-01T10:00:00Z")
    private String createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2023-02-01T12:00:00Z")
    private String updatedAt;

    @Schema(description = "Indica si el cliente ha sido eliminado", example = "false")
    private Boolean isDeleted;
}