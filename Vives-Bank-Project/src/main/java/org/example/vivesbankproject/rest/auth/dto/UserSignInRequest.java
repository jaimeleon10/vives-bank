package org.example.vivesbankproject.rest.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Representa la solicitud para el proceso de inicio de sesión (sign-in) de un usuario.
 * Contiene el nombre de usuario y la contraseña, con validaciones para asegurar la integridad de los datos.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInRequest {

    /**
     * Nombre de usuario proporcionado por el cliente para el inicio de sesión.
     * Este campo no puede estar vacío.
     */
    @NotBlank(message = "Username no puede estar vacío")
    @Schema(description = "Nombre de usuario para el inicio de sesión", example = "usuario123")
    private String username;

    /**
     * Contraseña proporcionada por el cliente para el inicio de sesión.
     * Este campo debe tener al menos 5 caracteres para garantizar la seguridad mínima.
     */
    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Schema(description = "Contraseña para el inicio de sesión", example = "pass12345")
    private String password;
}