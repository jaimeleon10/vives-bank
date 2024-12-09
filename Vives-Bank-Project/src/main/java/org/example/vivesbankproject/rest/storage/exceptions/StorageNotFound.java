package org.example.vivesbankproject.rest.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Excepción personalizada para representar errores de recurso no encontrado en el almacenamiento.
 * Esta clase extiende {@link StorageException} y está asociada con respuestas HTTP
 * de tipo {@link HttpStatus#NOT_FOUND}.
 * <p>
 * Se utiliza para capturar situaciones donde un recurso (archivo, carpeta, u otros elementos) no se encuentra.
 * </p>
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StorageNotFound extends StorageException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    /**
     * Constructor que recibe un mensaje como parámetro para describir el error de recurso no encontrado.
     *
     * @param mensaje Mensaje que proporciona información sobre el recurso no encontrado.
     */
    public StorageNotFound(String mensaje) {
        super(mensaje);
    }
}