package org.example.vivesbankproject.security.auth.services.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.security.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.security.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.security.auth.dto.UserSignUpRequest;
import org.example.vivesbankproject.security.auth.exceptions.AuthSingInInvalid;
import org.example.vivesbankproject.security.auth.exceptions.UserAuthNameOrEmailExisten;
import org.example.vivesbankproject.security.auth.exceptions.UserDiferentePasswords;
import org.example.vivesbankproject.security.auth.services.jwt.JwtService;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementación del servicio de autenticación para el registro de usuarios e inicio de sesión.
 * <p>
 * Esta clase implementa la lógica necesaria para el registro de usuarios, autenticación mediante
 * credenciales válidas, generación de tokens JWT y gestión de excepciones relacionadas con los procesos
 * de autenticación.
 * </p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository authUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationServiceImpl(UserRepository authUsersRepository, PasswordEncoder passwordEncoder,
                                     JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authUsersRepository = authUsersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Este método verifica si las contraseñas coinciden, codifica la contraseña, guarda el usuario en la base de datos
     * y devuelve un token JWT para el nuevo usuario registrado.
     * </p>
     *
     * @param request Detalles de la solicitud de registro, incluyendo nombre de usuario, contraseña y confirmación.
     * @return JwtAuthResponse Respuesta con el token JWT generado tras el registro exitoso.
     */
    @Override
    @Transactional
    @Operation(summary = "Registrar un nuevo usuario en el sistema", description = "Este endpoint registra un nuevo usuario si las credenciales son válidas.", responses = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "El usuario ya existe en la base de datos o las contraseñas no coinciden")
    })
    public JwtAuthResponse signUp(UserSignUpRequest request) {
        log.info("Creando usuario: {}", request);

        if (request.getPassword().contentEquals(request.getPasswordComprobacion())) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                    .build();
            try {
                var userStored = authUsersRepository.save(user);
                return JwtAuthResponse.builder().token(jwtService.generateToken(userStored)).build();
            } catch (DataIntegrityViolationException ex) {
                throw new UserAuthNameOrEmailExisten("El usuario con username " + request.getUsername() + " ya existe");
            }
        } else {
            throw new UserDiferentePasswords("Las contraseñas no coinciden");
        }
    }

    /**
     * Inicia sesión de un usuario con sus credenciales.
     * <p>
     * El método autentica al usuario con el administrador de autenticación y devuelve un token JWT si las credenciales
     * son correctas. De lo contrario, lanza una excepción en caso de fallo en la autenticación.
     * </p>
     *
     * @param request Datos de inicio de sesión, incluyendo el nombre de usuario y la contraseña.
     * @return JwtAuthResponse Respuesta con el token JWT generado tras el inicio de sesión exitoso.
     */
    @Override
    @Operation(summary = "Autenticar usuario", description = "Este endpoint autentica las credenciales de un usuario para iniciar sesión.", responses = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso con un token válido",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos")
    })
    public JwtAuthResponse signIn(UserSignInRequest request) {
        log.info("Autenticando usuario: {}", request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = authUsersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        var jwt = jwtService.generateToken(user);

        return JwtAuthResponse.builder().token(jwt).build();
    }
}