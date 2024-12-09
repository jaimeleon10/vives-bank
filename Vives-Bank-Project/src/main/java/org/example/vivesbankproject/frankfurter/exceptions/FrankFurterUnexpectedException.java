package org.example.vivesbankproject.frankfurter.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar errores inesperados al obtener las tasas de cambio
 * desde el servicio Frankfurter. Extiende la clase base {@link FrankFurterException}.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Devuelve un estado 400 en caso de error
public class FrankFurterUnexpectedException extends FrankFurterException {

    /**
     * Constructor para crear una excepción con un mensaje personalizado que indica un error inesperado.
     *
     * @param baseCurrency Moneda base en la solicitud de conversión.
     * @param targetCurrency Moneda objetivo en la solicitud de conversión.
     *
     * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    public FrankFurterUnexpectedException(
            @Schema(description = "Moneda base en la solicitud de conversión", example = "EUR") String baseCurrency,
            @Schema(description = "Moneda objetivo en la solicitud de conversión", example = "USD") String targetCurrency) {
        super(String.format("Error inesperado al obtener las tasas de cambio para %s a %s. " +
                        "Posibles causas: problema con el servicio Frankfurter, conexión a internet, " +
                        "o divisas incorrectas. Por favor, intente nuevamente más tarde.",
                baseCurrency, targetCurrency));
    }
}
