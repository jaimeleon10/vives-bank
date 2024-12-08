package org.example.vivesbankproject.rest.users.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundByUsername extends UserException {
    public UserNotFoundByUsername(String username) {
        super("Usuario con username '" + username + "' no encontrado");
    }
}