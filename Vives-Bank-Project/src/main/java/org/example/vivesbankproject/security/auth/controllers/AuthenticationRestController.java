package org.example.vivesbankproject.security.auth.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.security.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.security.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.security.auth.dto.UserSignUpRequest;
import org.example.vivesbankproject.security.auth.services.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador encargado de gestionar todas las operaciones relacionadas con la autenticación de usuarios,
 * como el registro de nuevos usuarios y el inicio de sesión.
 * Proporciona los endpoints para el proceso de autenticación usando un servicio de negocio.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("${api.version}/auth")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    /**
     * Constructor para la inyección de dependencias del servicio de autenticación.
     *
     * @param authenticationService Servicio encargado de la lógica de autenticación.
     */
    @Autowired
    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     * <p>
     * Recibe los datos de registro del usuario en el cuerpo de la solicitud. Se validan automáticamente con
     * la anotación @Valid. Devuelve un token JWT al usuario registrado.
     * </p>
     *
     * @param request Datos del nuevo usuario para el proceso de registro.
     * @return Respuesta HTTP con un token JWT generado para el nuevo usuario.
     */
    @Operation(summary = "Registrar usuario en el sistema",
            description = "Permite a un nuevo usuario registrarse en la aplicación y devuelve un token JWT en caso de éxito.")
    @ApiResponse(responseCode = "200", description = "Registro exitoso, token JWT devuelto")
    @PostMapping("/signup")
    public ResponseEntity<JwtAuthResponse> signUp(@Valid @RequestBody UserSignUpRequest request) {
        log.info("Registrando usuario: {}", request);
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    /**
     * Endpoint para iniciar sesión con las credenciales de un usuario existente.
     * <p>
     * Recibe los datos de inicio de sesión en el cuerpo de la solicitud. Se validan automáticamente con
     * la anotación @Valid. Devuelve un token JWT válido para el usuario autenticado.
     * </p>
     *
     * @param request Datos de inicio de sesión del usuario.
     * @return Respuesta HTTP con el token JWT generado para el usuario autenticado.
     */
    @Operation(summary = "Iniciar sesión en el sistema",
            description = "Permite a un usuario autenticarse en la aplicación con sus credenciales.")
    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, token JWT devuelto")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signIn(@Valid @RequestBody UserSignInRequest request) {
        log.info("Iniciando sesión de usuario: {}", request);
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    /**
     * Captura excepciones de validación al momento de enviar datos no válidos en la solicitud.
     * Devuelve los detalles de los campos erróneos y sus mensajes de validación para facilitar la corrección.
     *
     * @param ex Excepción lanzada por errores en la validación de campos.
     * @return Mapa con nombres de campos y sus respectivos mensajes de error.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Operation(summary = "Manejo de excepciones de validación",
            description = "Captura errores de validación en el proceso de envío de datos y devuelve información detallada de los mismos.")
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}