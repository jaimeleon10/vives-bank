package org.example.vivesbankproject.rest.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la respuesta devuelta después de una operación de autenticación exitosa (registro o inicio de sesión).
 * Contiene el token JWT generado que el cliente utilizará para autenticarse en futuras solicitudes.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    /**
     * Token JWT generado durante el proceso de autenticación.
     * Este token es necesario para acceder a los recursos protegidos de la API.
     */
    @Schema(description = "Token JWT generado para la autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    private String token;
}