package org.example.vivesbankproject.users.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFound extends UserException {
    public UserNotFound(UUID id) {
        super("Usuario con id '" + id + "' no encontrado");
    }

    public UserNotFound(String username) {
        super("Usuario con username '" + username + "' no encontrado");
    }
}