package org.example.vivesbankproject.security.auth.services.authentication;


import org.example.vivesbankproject.security.auth.dto.JwtAuthResponse;
import org.example.vivesbankproject.security.auth.dto.UserSignInRequest;
import org.example.vivesbankproject.security.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}