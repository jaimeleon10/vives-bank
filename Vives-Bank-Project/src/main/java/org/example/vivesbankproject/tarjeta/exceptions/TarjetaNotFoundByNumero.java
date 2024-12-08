package org.example.vivesbankproject.tarjeta.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra una tarjeta con un número específico.
 * Devuelve un estado HTTP 404 (Not Found) cuando se produce.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNotFoundByNumero extends TarjetaException {
    /**
     * Constructor que crea una excepción con un mensaje que incluye el número de tarjeta no encontrado.
     *
     * @param numeroTarjeta Número de tarjeta que no se pudo encontrar
     */
    public TarjetaNotFoundByNumero(String numeroTarjeta) {
        super("Tarjeta no encontrada con el numero de tarjeta: " + numeroTarjeta);
    }
}