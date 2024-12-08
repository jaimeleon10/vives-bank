package org.example.vivesbankproject.rest.auth.services.jwt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interfaz para la gestión de tokens JWT.
 * <p>
 * Esta interfaz proporciona métodos para generar, validar y extraer información de tokens JWT, que son
 * utilizados en el proceso de autenticación y autorización de usuarios.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface JwtService {

    /**
     * Extrae el nombre de usuario de un token JWT.
     *
     * @param token Token JWT del que se extraerá el nombre de usuario.
     * @return Nombre de usuario extraído del token.
     */
    @Operation(summary = "Extraer nombre de usuario de un token JWT", description = "Este método extrae el nombre de usuario del token JWT proporcionado.", responses = {
            @ApiResponse(responseCode = "200", description = "Nombre de usuario extraído exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    String extractUserName(String token);

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param userDetails Detalles del usuario para generar el token.
     * @return Token JWT generado.
     */
    @Operation(summary = "Generar token JWT para un usuario", description = "Este método genera un token JWT para el usuario autenticado utilizando sus detalles.", responses = {
            @ApiResponse(responseCode = "200", description = "Token generado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    })
    String generateToken(UserDetails userDetails);

    /**
     * Valida si el token JWT es válido para el usuario proporcionado.
     *
     * @param token Token JWT que se validará.
     * @param userDetails Detalles del usuario para la validación.
     * @return true si el token es válido, false en caso contrario.
     */
    @Operation(summary = "Validar la validez de un token JWT", description = "Este método verifica si un token JWT es válido para el usuario proporcionado.", responses = {
            @ApiResponse(responseCode = "200", description = "Token válido", content = @Content(mediaType = "application/json", schema = @Schema(type = "boolean"))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    boolean isTokenValid(String token, UserDetails userDetails);
}