package org.example.vivesbankproject.rest.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra una tarjeta con un identificador específico.
 * Devuelve un estado HTTP 404 (Not Found) cuando se produce.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNotFound extends TarjetaException {
    /**
     * Constructor que crea una excepción con un mensaje que incluye el identificador de la tarjeta no encontrada.
     *
     * @param id Identificador único (GUID) de la tarjeta que no se pudo encontrar
     */
    public TarjetaNotFound(String id) {
        super("Tarjeta no encontrada con guid: " + id);
    }
}