package org.example.vivesbankproject.frankfurter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FrankFurterConnectionException extends FrankFurterException {
    public FrankFurterConnectionException(String baseCurrency, String targetCurrencies, Throwable cause) {
        super(String.format("Error de conexión al obtener las tasas de cambio de %s a %s. Por favor, verifique su conexión a internet e intente nuevamente.\n"
                + "Tambien podria deberse a que FrankFurter ya no ofrece sus servicios.",
                baseCurrency, targetCurrencies, cause));
    }
}