package org.example.vivesbankproject.rest.storage.csvProductos.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Servicio para el procesamiento y almacenamiento de datos desde archivos CSV.
 * Esta interfaz define operaciones para importar archivos CSV y procesar sus datos.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface CsvStorageService {

    /**
     * Importa datos desde un archivo CSV y los guarda en la base de datos.
     *
     * El método procesa el archivo CSV proporcionado y almacena sus datos en la base de datos correspondiente.
     * Se espera que el archivo contenga un formato válido para su correcto procesamiento.
     *
     * @param file el archivo CSV que se enviará para su procesamiento y almacenamiento.
     * @throws IOException si ocurre un error al leer el contenido del archivo.
     * @throws IllegalArgumentException si el archivo tiene un formato inválido o no cumple con las expectativas definidas.
     */
    @Operation(
            summary = "Importar archivo CSV",
            description = "Procesa un archivo CSV y almacena sus datos en la base de datos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Archivo CSV importado correctamente",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Formato inválido en el archivo CSV",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error interno al procesar el archivo",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    void importCsv(MultipartFile file) throws IOException, IllegalArgumentException;
}