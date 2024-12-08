package org.example.vivesbankproject.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserExists extends UserException {
    public UserExists(String username) {
        super("El nombre de usuario '" + username + "' ya existe");
    }
}