package org.example.vivesbankproject.frankfurter.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FrankfurterEmptyResponseException extends FrankFurterException  {
    public FrankfurterEmptyResponseException(String baseCuerrency, String symbol, String amount) {super(
            "No se obtuvieron datos en la respuesta de Frankfurter para la moneda '" + baseCuerrency + "', simbolo '" + symbol + "', y cantidad '" + amount + "'"
    );}
}
