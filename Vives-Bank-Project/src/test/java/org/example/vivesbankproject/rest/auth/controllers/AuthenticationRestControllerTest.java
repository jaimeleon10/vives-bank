package org.example.vivesbankproject.rest.auth.controllers;

import org.example.vivesbankproject.rest.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.rest.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.rest.auth.dto.UserSignUpRequest;
import org.example.vivesbankproject.rest.auth.services.authentication.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationRestControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationRestController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp() {
        UserSignUpRequest request = new  UserSignUpRequest();
        request.setNombre("testuser");
        request.setUsername("testuser@example.com");
        request.setPassword("testpassword");

        JwtAuthResponse mockResponse = JwtAuthResponse.builder().token("mock-jwt-token").build();

        when(authenticationService.signUp(request)).thenReturn(mockResponse);

        ResponseEntity<JwtAuthResponse> response = authController.signUp(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("mock-jwt-token", response.getBody().getToken());
        verify(authenticationService, times(1)).signUp(request);
    }

    @Test
    void testSignIn() {
        UserSignInRequest request = new UserSignInRequest("testuser@example.com", "password");
        JwtAuthResponse mockResponse = JwtAuthResponse.builder().token("mock-jwt-token").build();

        when(authenticationService.signIn(request)).thenReturn(mockResponse);

        ResponseEntity<JwtAuthResponse> response = authController.signIn(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("mock-jwt-token", Objects.requireNonNull(response.getBody()).getToken());
        verify(authenticationService, times(1)).signIn(request);
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException mockEx = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(mockEx.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(java.util.Collections.singletonList(
                mock(org.springframework.validation.FieldError.class)
        ));

        Map<String, String> errors = authController.handleValidationExceptions(mockEx);

        assertNotNull(errors);
        assertFalse(errors.isEmpty());
    }
}