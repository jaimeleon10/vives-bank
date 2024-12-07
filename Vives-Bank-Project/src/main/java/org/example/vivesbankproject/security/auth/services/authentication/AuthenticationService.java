package org.example.vivesbankproject.security.auth.services.authentication;


import org.example.vivesbankproject.security.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.security.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.security.auth.dto.UserSignUpRequest;

/**
 * Servicio de autenticación para el registro de usuarios e inicio de sesión.
 * <p>
 * Esta interfaz define los métodos esenciales para el proceso de autenticación,
 * incluyendo el registro de nuevos usuarios y el inicio de sesión con credenciales válidas.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface AuthenticationService {

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Detalles de registro que contienen el nombre de usuario, contraseña y demás datos requeridos.
     * @return Respuesta con el token JWT generado tras el registro exitoso.
     */
    JwtAuthResponse signUp(UserSignUpRequest request);

    /**
     * Inicia sesión de un usuario existente en el sistema.
     *
     * @param request Detalles de inicio de sesión que contienen el nombre de usuario y la contraseña.
     * @return Respuesta con el token JWT generado tras el inicio de sesión exitoso.
     */
    JwtAuthResponse signIn(UserSignInRequest request);
}