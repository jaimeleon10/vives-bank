package org.example.vivesbankproject.tarjeta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Representa un objeto de solicitud para operaciones de tarjeta privada.
 * Esta clase encapsula el nombre de usuario y la contraseña requeridos para la autenticación
 * en transacciones relacionadas con tarjetas privadas.
 *
 * @author Jaime León, Natalia González, German Fernandez,Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
public class TarjetaRequestPrivado {

    /**
     * El nombre de usuario del titular de la tarjeta.
     * Este campo no puede estar vacío.
     */
    @Schema(description = "Nombre de usuario del titular de la tarjeta", example = "user1", required = true)
    @NotBlank(message = "El usuario no puede estar vacío")
    private String username;

    /**
     * La contraseña asociada con la cuenta del usuario.
     * Este campo no puede estar vacío.
     */
    @Schema(description = "Contraseña asociada con el usuario", example = "password123", required = true)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String userPass;
}
