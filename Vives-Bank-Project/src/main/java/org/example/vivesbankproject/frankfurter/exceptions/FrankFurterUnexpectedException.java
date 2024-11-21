package org.example.vivesbankproject.frankfurter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FrankFurterUnexpectedException extends FrankFurterException{
    public FrankFurterUnexpectedException(String baseCurrency, String targetCurrency) {
        super(String.format("Error inesperado al obtener las tasas de cambio para %s a %s. " +
                        "Posibles causas: problema con el servicio Frankfurter, conexión a internet, " +
                        "o divisas incorrectas. Por favor, intente nuevamente más tarde.",
                baseCurrency, targetCurrency));
    }
}
