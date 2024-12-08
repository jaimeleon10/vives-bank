package org.example.vivesbankproject.rest.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Excepción personalizada para manejar solicitudes inválidas en el almacenamiento de datos.
 * Esta clase extiende la clase base de excepciones {@link StorageException} y devuelve el
 * estado HTTP 400 en caso de error en la solicitud.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageBadRequest extends StorageException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    /**
     * Constructor que recibe un mensaje como parámetro.
     *
     * @param mensaje Mensaje con información sobre la razón del error.
     */
    public StorageBadRequest(String mensaje) {
        super(mensaje);
    }
}