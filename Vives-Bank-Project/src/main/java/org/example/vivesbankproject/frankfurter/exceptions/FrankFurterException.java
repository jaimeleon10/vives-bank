package org.example.vivesbankproject.frankfurter.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Clase base para las excepciones personalizadas relacionadas con el servicio FrankFurter.
 * Esta clase extiende {@link RuntimeException} para capturar errores específicos en la lógica de negocio.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Devuelve un estado 400 en caso de error
public abstract class FrankFurterException extends RuntimeException {

    /**
     * Constructor para crear una excepción personalizada con un mensaje de error.
     *
     * @param message Mensaje de error que describe el problema o excepción específica.
     *
     * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    public FrankFurterException(
            @Schema(description = "Mensaje detallado de la excepción", example = "Error al procesar la solicitud de conversión de divisas.") String message) {
        super(message);
    }
}
