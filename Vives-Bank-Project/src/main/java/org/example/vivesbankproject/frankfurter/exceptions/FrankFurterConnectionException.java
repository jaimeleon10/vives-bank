package org.example.vivesbankproject.frankfurter.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar los errores de conexión con el servicio FrankFurter
 * al intentar obtener tasas de cambio entre dos monedas.
 * Esta excepción indica que no se pudo establecer la conexión con el servicio externo.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE) // Devuelve un estado 503 al ocurrir la excepción
public class FrankFurterConnectionException extends FrankFurterException {

    /**
     * Constructor para crear una excepción con detalles sobre la moneda base, las monedas destino
     * y la causa que originó el fallo.
     *
     * @param baseCurrency Moneda base desde la que se intenta realizar la conversión.
     * @param targetCurrencies Monedas destino para las cuales se intenta realizar la conversión.
     * @param cause Causa original que disparó la excepción.
     *
     * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    public FrankFurterConnectionException(
            @Schema(description = "Moneda base desde la que se intenta obtener la conversión", example = "EUR") String baseCurrency,
            @Schema(description = "Monedas destino para las cuales se intenta obtener la conversión", example = "USD,GBP") String targetCurrencies,
            Throwable cause) {
        super(String.format("Error de conexión al obtener las tasas de cambio de %s a %s. Por favor, verifique su conexión a internet e intente nuevamente.\n"
                        + "También podría deberse a que FrankFurter ya no ofrece sus servicios.",
                baseCurrency, targetCurrencies, cause));
    }
}