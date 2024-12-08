package org.example.vivesbankproject.rest.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

/**
 * Excepción personalizada para representar errores internos en el almacenamiento.
 * Esta clase extiende {@link StorageException} y está asociada con respuestas HTTP
 * de tipo {@link HttpStatus#INTERNAL_SERVER_ERROR}.
 * <p>
 * Se utiliza para capturar situaciones inesperadas que ocurren durante operaciones de almacenamiento.
 * </p>
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageInternal extends StorageException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    /**
     * Constructor que recibe un mensaje como parámetro para describir el error interno.
     *
     * @param mensaje Mensaje que proporciona información sobre el error interno.
     */
    public StorageInternal(String mensaje) {
        super(mensaje);
    }
}