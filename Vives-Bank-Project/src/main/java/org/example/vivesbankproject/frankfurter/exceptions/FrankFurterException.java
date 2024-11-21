package org.example.vivesbankproject.frankfurter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public abstract class FrankFurterException extends RuntimeException  {
    public FrankFurterException(String message) {
        super(message);
    }
}
