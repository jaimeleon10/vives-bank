package org.example.vivesbankproject.storage.csvProductos.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CsvStorageService {

    /**
     * Importa datos desde un archivo CSV y los guarda en la base de datos.
     *
     * @param file el archivo CSV a procesar.
     * @throws IOException si ocurre un error al leer el archivo.
     * @throws IllegalArgumentException si el archivo tiene un formato inválido.
     */
    void importCsv(MultipartFile file) throws IOException;
}