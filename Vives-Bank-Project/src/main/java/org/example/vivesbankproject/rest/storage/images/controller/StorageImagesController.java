package org.example.vivesbankproject.rest.storage.images.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.storage.images.services.StorageImagesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Controlador que gestiona las operaciones de almacenamiento de imágenes,
 * como cargar fotos de identificación y fotos de perfil. Ofrece rutas
 * para acceder a estos recursos desde el almacenamiento local.
 * <p>
 * Este controlador utiliza el servicio {@link StorageImagesService} para realizar las operaciones.
 * </p>
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@Slf4j
@RequestMapping("/storage/images")
public class StorageImagesController {

    private final StorageImagesService storageService;

    /**
     * Constructor que inyecta el servicio de almacenamiento de imágenes.
     *
     * @param storageImagesService Servicio para operaciones de almacenamiento de imágenes.
     */
    @Autowired
    public StorageImagesController(StorageImagesService storageImagesService) {
        this.storageService = storageImagesService;
    }

    /**
     * Endpoint para recuperar la foto de identificación (DNI) desde el almacenamiento.
     *
     * @param filename Nombre del archivo en el almacenamiento.
     * @param request Objeto HttpServletRequest para determinar el tipo MIME.
     * @return ResponseEntity con el recurso encontrado en el almacenamiento.
     */
    @GetMapping(value = "/dni/{filename:.+}")
    @ResponseBody
    @Operation(
            summary = "Obtiene la foto de identificación (DNI) desde el almacenamiento",
            description = "Recupera la foto de identificación especificada desde el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Foto de identificación recuperada correctamente.",
                            content = @Content(mediaType = "application/octet-stream", examples = {
                                    @ExampleObject(value = "Imagen cargada correctamente")
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "No se encuentra el recurso en el almacenamiento")
            }
    )
    public ResponseEntity<Resource> UploadFotoDni(@PathVariable String filename, HttpServletRequest request) {
        return getResourceResponseEntity(filename, request);
    }

    /**
     * Endpoint para recuperar la foto de perfil desde el almacenamiento.
     *
     * @param filename Nombre del archivo en el almacenamiento.
     * @param request Objeto HttpServletRequest para determinar el tipo MIME.
     * @return ResponseEntity con el recurso encontrado en el almacenamiento.
     */
    @GetMapping(value = "/imgPerfil/{filename:.+}")
    @ResponseBody
    @Operation(
            summary = "Obtiene la foto de perfil desde el almacenamiento",
            description = "Recupera la foto de perfil especificada desde el almacenamiento local.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Foto de perfil recuperada correctamente.",
                            content = @Content(mediaType = "application/octet-stream", examples = {
                                    @ExampleObject(value = "Imagen cargada correctamente")
                            })
                    ),
                    @ApiResponse(responseCode = "404", description = "No se encuentra el recurso en el almacenamiento")
            }
    )
    public ResponseEntity<Resource> UploadFotoPerfil(@PathVariable String filename, HttpServletRequest request) {
        return getResourceResponseEntity(filename, request);
    }

    /**
     * Método privado para centralizar la lógica de recuperación de recursos desde el almacenamiento.
     *
     * @param filename Nombre del recurso en el almacenamiento.
     * @param request Objeto HttpServletRequest para determinar el tipo MIME.
     * @return ResponseEntity con la información de tipo de contenido y el recurso solicitado.
     */
    @NotNull
    private ResponseEntity<Resource> getResourceResponseEntity(@PathVariable String filename, HttpServletRequest request) {
        Resource file = storageService.loadAsResource(filename);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("No se puede determinar el tipo de contenido del fichero");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}