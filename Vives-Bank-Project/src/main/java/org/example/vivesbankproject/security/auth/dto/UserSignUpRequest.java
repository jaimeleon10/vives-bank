package org.example.vivesbankproject.security.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Representa la solicitud para el proceso de registro (sign-up) de un nuevo usuario.
 * Contiene la información necesaria para el registro, como el nombre, nombre de usuario,
 * contraseña y la confirmación de la contraseña.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {

    /**
     * Nombre completo del usuario que intenta registrarse.
     * Este campo no puede estar vacío.
     */
    @NotBlank(message = "Nombre no puede estar vacío")
    @Schema(description = "Nombre completo del usuario para el proceso de registro", example = "Juan Pérez")
    private String nombre;

    /**
     * Nombre de usuario elegido por el usuario para el registro.
     * Este campo no puede estar vacío.
     */
    @NotBlank(message = "Username no puede estar vacío")
    @Schema(description = "Nombre de usuario para el registro", example = "juanperez")
    private String username;

    /**
     * Contraseña que el usuario desea usar para el registro.
     * Debe tener al menos 5 caracteres para garantizar la seguridad mínima.
     */
    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
    @Schema(description = "Contraseña para el registro del usuario", example = "pass12345")
    private String password;

    /**
     * Confirmación de la contraseña para verificar que la contraseña introducida es correcta.
     * Debe tener al menos 5 caracteres para garantizar la seguridad mínima.
     */
    @NotBlank(message = "Password no puede estar vacío")
    @Length(min = 5, message = "Password de comprobación debe tener al menos 5 caracteres")
    @Schema(description = "Contraseña de comprobación para el proceso de registro", example = "pass12345")
    private String passwordComprobacion;
}