
package org.example.vivesbankproject.cliente.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.service.ClienteService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * Controlador de clientes del tipo RestController
 * Fijamos la ruta de acceso a este controlador.
 * Utilizamos el servicio de clientes y herramientas de paginación inyectados a través del constructor.
 *
 * @Autowired es una anotación que permite inyectar dependencias basadas en las anotaciones
 * @Controller, @Service, @Component, etc., que se encuentran en el contenedor de Spring.
 */
@RestController
@RequestMapping("${api.version}/clientes")
@Tag(name = "Clientes", description = "Endpoint para gestionar clientes")
@Validated
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ClienteRestController {
    // Servicio de clientes
    private final ClienteService clienteService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public ClienteRestController(ClienteService clienteService, PaginationLinksUtils paginationLinksUtils) {
        this.clienteService = clienteService;
        this.paginationLinksUtils = paginationLinksUtils;
    }
    /**
     * Obtiene todos los clientes con opciones de filtrado y paginación.
     *
     * @param dni       Filtra por DNI del cliente
     * @param nombre    Filtra por nombre del cliente
     * @param apellido  Filtra por apellido del cliente
     * @param email     Filtra por email del cliente
     * @param telefono  Filtra por teléfono del cliente
     * @param page      Número de página (por defecto: 0)
     * @param size      Tamaño de la página (por defecto: 10)
     * @param sortBy    Campo para ordenar (por defecto: id)
     * @param direction Dirección de orden (ascendente o descendente, por defecto: asc)
     * @return Página de clientes filtrados
     */
    @Operation(summary = "Obtiene todos los clientes", description = "Devuelve una lista paginada de clientes con filtros opcionales.")
    @Parameters({
            @Parameter(name = "dni", description = "DNI del cliente", example = "12345678A"),
            @Parameter(name = "nombre", description = "Nombre del cliente", example = "Juan"),
            @Parameter(name = "apellido", description = "Apellido del cliente", example = "Pérez"),
            @Parameter(name = "email", description = "Email del cliente", example = "juan.perez@gmail.com"),
            @Parameter(name = "telefono", description = "Teléfono del cliente", example = "600123456"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de clientes")
    })

    @GetMapping
    public ResponseEntity<PageResponse<ClienteResponse>> getAll(

            @RequestParam(required = false) Optional<String> dni,
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> apellido,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<String> telefono,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<ClienteResponse> pageResult = clienteService.getAll(dni,nombre, apellido, email,telefono, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un cliente por su ID.
     *
     * @param id Identificador del cliente
     * @return Cliente si existe
     */
    @Operation(summary = "Obtiene un cliente por su ID", description = "Devuelve un cliente a partir de su identificador único.")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del cliente", example = "1", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })

    @GetMapping("{id}")
    public ResponseEntity<ClienteResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(clienteService.getById(id));
    }

    /**
     * Obtiene un cliente por su DNI.
     *
     * @param dni DNI del cliente.
     * @return Cliente asociado al DNI.
     */
    @Operation(summary = "Obtiene un cliente por DNI", description = "Permite obtener los datos de un cliente dado su DNI.")

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ClienteResponse> getByDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.getByDni(dni));
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param clienteRequestSave Datos del cliente a crear
     * @return Cliente creado
     */
    @Operation(summary = "Crea un cliente", description = "Añade un nuevo cliente a la base de datos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del cliente a crear", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Datos no válidos para el cliente")
    })

    @PostMapping
    public ResponseEntity<ClienteResponse> save(@Valid @RequestBody ClienteRequestSave clienteRequestSave) {
        var result = clienteService.save(clienteRequestSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Actualiza un cliente existente.
     *
     * @param id                  Identificador del cliente a actualizar
     * @param clienteRequestUpdate Datos actualizados del cliente
     * @return Cliente actualizado
     */
    @Operation(summary = "Actualiza un cliente", description = "Modifica los datos de un cliente existente en la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })

    @PutMapping("{id}")
    public ResponseEntity<ClienteResponse> update(@PathVariable String id, @Valid @RequestBody ClienteRequestUpdate clienteRequestUpdate) {
        var result = clienteService.update(id, clienteRequestUpdate);
        return ResponseEntity.ok(result);
    }

    /**
     * Elimina un cliente por su ID.
     *
     * @param id Identificador del cliente
     * @return Respuesta sin contenido
     */
    @Operation(summary = "Elimina un cliente", description = "Borra un cliente de la base de datos a partir de su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene el perfil del cliente autenticado.
     *
     * @param user Usuario autenticado
     * @return Perfil del cliente
     */
    @Operation(summary = "Obtiene el perfil del cliente autenticado", description = "Obtiene los datos del perfil del cliente actualmente autenticado.")
    @GetMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clienteService.getUserAuthenticatedByGuid(user.getGuid()));
    }

    /**
     * Actualiza el perfil del cliente autenticado.
     *
     * @param user                  Usuario autenticado.
     * @param clienteRequestUpdate  Datos actualizados del perfil.
     * @return Perfil actualizado.
     */
    @Operation(summary = "Actualiza el perfil del cliente autenticado", description = "Permite al cliente actualizar su perfil.")

    @PutMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteResponse> updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody ClienteRequestUpdate clienteRequestUpdate) {
        var result = clienteService.updateUserAuthenticated(user.getGuid(), clienteRequestUpdate);
        return ResponseEntity.ok(result);
    }

    /**
     * Elimina la cuenta del cliente autenticado (derecho al olvido).
     *
     * @param user Usuario autenticado.
     * @return Mensaje de confirmación.
     */
    @Operation(summary = "Elimina la cuenta del cliente autenticado", description = "Permite al cliente solicitar la eliminación de su cuenta.")

    @DeleteMapping("/me/perfil")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteMe(@AuthenticationPrincipal User user) {
        var result = clienteService.derechoAlOlvido(user.getGuid());
        return ResponseEntity.ok(result);
    }

    /**
     * Actualiza la imagen del DNI del cliente autenticado.
     *
     * @param user Usuario autenticado.
     * @param file Archivo de la imagen del DNI.
     * @return Perfil actualizado con la nueva imagen del DNI.
     */
    @Operation(summary = "Actualiza la imagen del DNI", description = "Permite al cliente actualizar la imagen de su DNI.")

    @PutMapping("/me/dni_image")
    public ResponseEntity<ClienteResponse> updateDniImage(@AuthenticationPrincipal User user, MultipartFile file) {
        log.info("Actualizando imagen dni del cliente con guid: {}", user.getGuid());
        return ResponseEntity.ok(clienteService.updateDniFoto(user.getGuid(), file));
    }

    /**
     * Actualiza la foto de perfil del cliente autenticado.
     *
     * @param user Usuario autenticado.
     * @param file Archivo de la nueva foto de perfil.
     * @return Perfil actualizado con la nueva foto.
     */
    @Operation(summary = "Actualiza la foto de perfil", description = "Permite al cliente actualizar su foto de perfil.")

    @PutMapping("/me/foto_perfil")
    public ResponseEntity<ClienteResponse> updateFotoPerfil(@AuthenticationPrincipal User user, MultipartFile file) {
        log.info("Actualizando imagen de perfil del cliente con guid: {}", user.getGuid());
        return ResponseEntity.ok(clienteService.updateProfileFoto(user.getGuid(), file));
    }

    /**
     * Obtiene el catálogo de productos del cliente.
     *
     * @return Catálogo de productos.
     */
    @Operation(summary = "Obtiene el catálogo de productos", description = "Obtiene el catálogo de productos disponibles para el cliente.")

    @GetMapping("/catalogo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ClienteProducto> getCatalogue() {
        return ResponseEntity.ok(clienteService.getCatalogue());
    }

    /**
     * Maneja excepciones de validación.
     *
     * @param ex Excepción lanzada
     * @return Mapa de errores
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Map<String, String> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            methodArgumentNotValidException.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            constraintViolationException.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        return errors;
    }
}