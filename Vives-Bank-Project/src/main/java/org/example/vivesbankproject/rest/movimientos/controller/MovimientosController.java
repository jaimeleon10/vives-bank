package org.example.vivesbankproject.rest.movimientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosService;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosServiceImpl;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
/**
 * Controlador para la gestión de movimientos. Proporciona endpoints para operaciones CRUD relacionadas
 * con los movimientos. Los endpoints están protegidos con seguridad para el rol ADMIN.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("${api.version}/movimientos")
@Slf4j
@PreAuthorize("hasRole('ADMIN')")

public class MovimientosController {

    private final MovimientosService service;
    private final PaginationLinksUtils paginationLinksUtils;

    /**
     * Constructor para inyectar las dependencias requeridas.
     *
     * @param service             Servicio que contiene la lógica de negocio para operaciones relacionadas con movimientos.
     * @param paginationLinksUtils Utilidad para la creación de enlaces de paginación.
     */
    @Autowired
    public MovimientosController(MovimientosServiceImpl service, PaginationLinksUtils paginationLinksUtils) {
        this.paginationLinksUtils = paginationLinksUtils;
        this.service = service;
    }

    /**
     * Obtiene una lista paginada de movimientos con opciones de ordenamiento.
     *
     * @param page     El número de la página para la paginación (por defecto es 0).
     * @param size     El tamaño de la página para la paginación (por defecto es 10).
     * @param sortBy   El campo por el que se desea ordenar (por defecto es "id").
     * @param direction La dirección del ordenamiento, ya sea ascendente o descendente (por defecto es "asc").
     * @param request  El objeto HttpServletRequest para obtener la URL base de la solicitud.
     * @return ResponseEntity con la lista paginada de movimientos.
     */
    @Operation(
            summary = "Obtener todos los movimientos",
            description = "Recupera una lista paginada de movimientos con opciones de filtrado avanzado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de movimientos recuperada exitosamente",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<PageResponse<MovimientoResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Obteniendo todos los movimientos");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        var movimientos = service.getAll(pageable);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(movimientos, uriBuilder))
                .body(PageResponse.of(movimientos, sortBy, direction));
    }

    /**
     * Obtiene un movimiento por su identificador único (GUID).
     *
     * @param guid El identificador único del movimiento.
     * @return ResponseEntity con el movimiento encontrado.
     */
    @Operation(
            summary = "Obtener movimiento por GUID",
            description = "Recupera un movimiento específico mediante su identificador único"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{guid}")
    public ResponseEntity<MovimientoResponse> getById(@PathVariable String guid) {
        log.info("Obteniendo movimiento con guid: {}", guid);
        MovimientoResponse movimiento = service.getByGuid(guid);
        return ResponseEntity.ok(movimiento);
    }

    /**
     * Obtiene un movimiento basado en el identificador de cliente.
     *
     * @param clienteId El identificador del cliente.
     * @return ResponseEntity con la información del movimiento.
     */
    @Operation(
            summary = "Obtener movimiento por ID de cliente",
            description = "Recupera un movimiento específico utilizando el identificador del cliente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<MovimientoResponse> getByClienteGuid(@PathVariable String clienteId) {
        log.info("Obteniendo movimiento con id de cliente: {}", clienteId);
        MovimientoResponse movimiento = service.getByClienteGuid(clienteId);
        return ResponseEntity.ok(movimiento);
    }

    /**
     * Crea o actualiza un movimiento en la base de datos.
     *
     * @param movimiento El objeto MovimientoRequest con la información del movimiento a guardar.
     * @return ResponseEntity con el movimiento guardado/actualizado.
     */
    @Operation(
            summary = "Guardar movimiento",
            description = "Crea o actualiza un movimiento en la base de datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento guardado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MovimientoResponse> save(@RequestBody MovimientoRequest movimiento) {
        log.info("Creando/actualizando movimiento: {}", movimiento);
        MovimientoResponse savedMovimiento = service.save(movimiento);
        return ResponseEntity.ok(savedMovimiento);
    }

    /**
     * Manejador de excepciones para validaciones en el cuerpo de la solicitud.
     *
     * @param ex La excepción capturada durante la validación.
     * @return Un mapa con los errores de validación por campo.
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