package org.example.vivesbankproject.rest.auth.services.authentication;

import org.example.vivesbankproject.rest.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.rest.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.rest.auth.dto.UserSignUpRequest;
import org.example.vivesbankproject.rest.auth.exceptions.AuthSingInInvalid;
import org.example.vivesbankproject.rest.auth.exceptions.UserAuthNameOrEmailExisten;
import org.example.vivesbankproject.rest.auth.exceptions.UserDiferentePasswords;
import org.example.vivesbankproject.rest.auth.services.jwt.JwtService;
import org.example.vivesbankproject.rest.users.models.Role;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock
    private UserRepository authUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Juan Pérez")
                .username("testuser")
                .password("testpassword")
                .passwordComprobacion("testpassword")
                .build();

        User user = User.builder()
                .username(request.getUsername())
                .password("encoded-password")
                .roles(Stream.of(Role.USER).collect(Collectors.toSet()))
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(authUsersRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        JwtAuthResponse response = authService.signUp(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        verify(passwordEncoder).encode(request.getPassword());
        verify(authUsersRepository).save(any(User.class));
    }

    @Test
    void testSignUp_PasswordDistinta() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Juan Pérez")
                .username("testuser")
                .password("password1")
                .passwordComprobacion("password2")
                .build();

        assertThrows(UserDiferentePasswords.class, () -> {
            authService.signUp(request);
        });
    }

    @Test
    void testSignUp_UserExistente() {
        UserSignUpRequest request = UserSignUpRequest.builder()
                .nombre("Juan Pérez")
                .username("testuser")
                .password("testpassword")
                .passwordComprobacion("testpassword")
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(authUsersRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("User exists"));

        assertThrows(UserAuthNameOrEmailExisten.class, () -> {
            authService.signUp(request);
        });
    }

    @Test
    void testSignIn() {
        // Arrange
        UserSignInRequest request = new UserSignInRequest("testuser", "password");
        User user = User.builder()
                .username(request.getUsername())
                .build();

        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mock-jwt-token");

        // Act
        JwtAuthResponse response = authService.signIn(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testSignIn_CredencialesInvalidas() {
        UserSignInRequest request = new UserSignInRequest("invalidUser", "wrongPassword");

        // Simula que no se encuentra el usuario en el repositorio
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(AuthSingInInvalid.class, () -> {
            authService.signIn(request);
        });

        // Verifica que el método de autenticación fue llamado
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verifica que el repositorio fue consultado para el usuario
        verify(authUsersRepository).findByUsername(request.getUsername());
    }

}