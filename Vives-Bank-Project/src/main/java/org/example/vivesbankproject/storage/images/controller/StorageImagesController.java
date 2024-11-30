package org.example.vivesbankproject.storage.images.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.storage.images.services.StorageImagesService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/storage/images")
public class StorageImagesController {
    private final StorageImagesService storageService;

    @Autowired
    public StorageImagesController(StorageImagesService storageImagesService) {
        this.storageService = storageImagesService;
    }

    @GetMapping(value = "/dni/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> UploadFotoDni(@PathVariable String filename, HttpServletRequest request) {
        return getResourceResponseEntity(filename, request);
    }

    @GetMapping(value = "/imgPerfil/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> UploadFotoPerfil(@PathVariable String filename, HttpServletRequest request) {
        return getResourceResponseEntity(filename, request);
    }

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