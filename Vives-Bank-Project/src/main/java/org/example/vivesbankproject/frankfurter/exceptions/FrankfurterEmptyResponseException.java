package org.example.vivesbankproject.frankfurter.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar respuestas vacías por parte del servicio Frankfurter
 * cuando no se obtienen datos en la consulta de tasas de cambio.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Devuelve un estado 400 en caso de error
public class FrankfurterEmptyResponseException extends FrankFurterException {

    /**
     * Constructor para crear una excepción con detalles sobre la moneda base, el símbolo y la cantidad
     * que no devolvieron una respuesta válida desde el servicio Frankfurter.
     *
     * @param baseCurrency Moneda base que se intentó consultar en la respuesta de Frankfurter.
     * @param symbol Símbolo de la moneda objetivo para el cálculo.
     * @param amount Cantidad de la moneda consultada.
     *
     * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    public FrankfurterEmptyResponseException(
            @Schema(description = "Moneda base en la consulta de tasa de cambio", example = "EUR") String baseCurrency,
            @Schema(description = "Símbolo de la moneda objetivo", example = "USD") String symbol,
            @Schema(description = "Cantidad consultada en la solicitud", example = "1") String amount) {
        super(
                "No se obtuvieron datos en la respuesta de Frankfurter para la moneda '" + baseCurrency + "', símbolo '" + symbol + "', y cantidad '" + amount + "'"
        );
    }
}