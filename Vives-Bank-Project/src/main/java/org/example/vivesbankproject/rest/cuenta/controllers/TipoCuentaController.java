package org.example.vivesbankproject.rest.cuenta.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.rest.cuenta.services.TipoCuentaService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * Controlador para la gestión de tipos de cuentas del tipo RestController
 * Fijamos la ruta de acceso a este controlador.
 * Utiliza el servicio de tipos de cuentas y herramientas de paginación inyectadas a través del constructor.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("${api.version}/tipocuentas")
@Slf4j
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class TipoCuentaController {
    private final TipoCuentaService tipoCuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public TipoCuentaController(TipoCuentaService tipoCuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.tipoCuentaService = tipoCuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }
    /**
     * Obtiene todos los tipos de cuentas con opciones de filtrado y paginación.
     *
     * @param nombre      Nombre del tipo de cuenta para filtrar
     * @param interesMax  Interés máximo para filtrar
     * @param interesMin  Interés mínimo para filtrar
     * @param page        Página para paginación (por defecto: 0)
     * @param size        Tamaño de la página para paginación (por defecto: 10)
     * @param sortBy      Campo por el que ordenar (por defecto: id)
     * @param direction   Orden ascendente o descendente (por defecto: asc)
     * @param request     Solicitud HTTP
     * @return Lista paginada de tipos de cuentas filtrados
     */
    @Operation(summary = "Obtiene todos los tipos de cuentas", description = "Devuelve una lista paginada de tipos de cuentas con filtros opcionales.")
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del tipo de cuenta para buscar", example = "AHORROS"),
            @Parameter(name = "interesMax", description = "Interés máximo", example = "10"),
            @Parameter(name = "interesMin", description = "Interés mínimo", example = "1"),
            @Parameter(name = "page", description = "Número de página para paginación", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página para paginación", example = "10"),
            @Parameter(name = "sortBy", description = "Campo para ordenar", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenamiento", example = "asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de tipos de cuentas obtenida correctamente")
    })
    @GetMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<TipoCuentaResponse>> getAll(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<BigDecimal> interesMax,
            @RequestParam(required = false) Optional<BigDecimal> interesMin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Buscando todos los tipos de cuentas con las siguientes opciones: {}, {}, {}", nombre, interesMax, interesMin);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<TipoCuentaResponse> pageResult = tipoCuentaService.getAll(nombre, interesMax, interesMin, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene un tipo de cuenta por su identificador.
     *
     * @param id Identificador del tipo de cuenta
     * @return Respuesta con el tipo de cuenta correspondiente
     */
    @Operation(summary = "Obtiene un tipo de cuenta por su identificador", description = "Devuelve información de un tipo de cuenta por su identificador.")

    @GetMapping("{id}")
    public ResponseEntity<TipoCuentaResponse> getById(@PathVariable String id) {
        log.info("Buscando el tipo de cuenta con id: {}", id);
        return ResponseEntity.ok(tipoCuentaService.getById(id));
    }
    /**
     * Crea un nuevo tipo de cuenta.
     *
     * @param tipoCuentaRequest Datos del tipo de cuenta para crear
     * @return Respuesta con el nuevo tipo de cuenta creado
     */
    @Operation(summary = "Crea un nuevo tipo de cuenta", description = "Crea un nuevo tipo de cuenta en la base de datos.")
    @PostMapping
    public ResponseEntity<TipoCuentaResponse> save(@Valid @RequestBody TipoCuentaRequest tipoCuentaRequest) {
        log.info("Creando nuevo tipo de cuenta: {}", tipoCuentaRequest);
        var result = tipoCuentaService.save(tipoCuentaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    /**
     * Actualiza un tipo de cuenta existente.
     *
     * @param id               Identificador del tipo de cuenta
     * @param tipoCuentaRequest Datos actualizados del tipo de cuenta
     * @return Respuesta con los detalles actualizados
     */
    @Operation(summary = "Actualiza un tipo de cuenta", description = "Actualiza la información de un tipo de cuenta en la base de datos.")
    @PutMapping("{id}")
    public ResponseEntity<TipoCuentaResponse> update(@PathVariable String id, @Valid @RequestBody TipoCuentaRequest tipoCuentaRequest) {
        log.info("Actualizando tipo de cuenta con id: {}", id);
        var result = tipoCuentaService.update(id, tipoCuentaRequest);
        return ResponseEntity.ok(result);
    }
    /**
     * Elimina un tipo de cuenta por su identificador.
     *
     * @param id Identificador del tipo de cuenta
     * @return Respuesta con el tipo de cuenta eliminado
     */
    @Operation(summary = "Elimina un tipo de cuenta", description = "Elimina un tipo de cuenta de la base de datos por su identificador.")
    @DeleteMapping("{id}")
    public ResponseEntity<TipoCuentaResponse> delete(@PathVariable String id) {
        log.info("Eliminando tipo de cuenta con id: {}", id);
        var result = tipoCuentaService.deleteById(id);
        return ResponseEntity.ok(result);
    }
    /**
     * Maneja excepciones de validación de datos.
     *
     * @param ex Excepción lanzada por la validación
     * @return Mapa con los errores encontrados
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