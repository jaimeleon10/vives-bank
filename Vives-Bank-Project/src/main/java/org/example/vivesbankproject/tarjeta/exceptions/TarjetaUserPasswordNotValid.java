package org.example.vivesbankproject.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaUserPasswordNotValid extends TarjetaException {
    public TarjetaUserPasswordNotValid() {
        super("La contraseña introducida no es correcta");
    }

}
