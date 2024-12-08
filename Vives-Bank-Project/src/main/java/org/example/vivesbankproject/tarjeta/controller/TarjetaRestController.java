package org.example.vivesbankproject.tarjeta.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.tarjeta.dto.*;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para la gestión de tarjetas bancarias.
 * Proporciona endpoints para operaciones CRUD de tarjetas.
 * Requiere rol de administrador para acceder a los métodos.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("${api.version}/tarjetas")
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Tarjetas", description = "Operaciones CRUD sobre tarjetas bancarias")
public class TarjetaRestController {

    private final TarjetaService tarjetaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public TarjetaRestController(TarjetaService tarjetaService, PaginationLinksUtils paginationLinksUtils) {
        this.tarjetaService = tarjetaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Endpoint devuelve una lista paginada de tarjetas que cumplen con los filtros,
     * ordenados por el campo especificado en el parámetro 'sortBy' en la
     * dirección indicada en el parámetro 'direction'. Si no se proporciona,
     * se ordena por el campo 'id' en orden ascendente.
     *
     * @param numero Filtro opcional por número de tarjeta.
     * @param caducidad Filtro opcional por fecha de caducidad.
     * @param tipoTarjeta Filtro opcional por tipo de tarjeta.
     * @param minLimiteDiario Filtro opcional por límite diario mínimo.
     * @param maxLimiteDiario Filtro opcional por límite diario máximo.
     * @param minLimiteSemanal Filtro opcional por límite semanal mínimo.
     * @param maxLimiteSemanal Filtro opcional por límite semanal máximo.
     * @param minLimiteMensual Filtro opcional por límite mensual mínimo.
     * @param maxLimiteMensual Filtro opcional por límite mensual máximo.
     * @param page Página de la lista a obtener. Si no se proporciona, se devuelve la primera página.
     * @param size Número de elementos a incluir en la lista por página. Si no se proporciona, se devuelve una lista de 10 elementos.
     * @param sortBy Campo por el que se ordena la lista. Si no se proporciona, se ordena por el campo 'id'.
     * @param direction Dirección en la que se ordena la lista. Si no se proporciona, se ordena en orden ascendente.
     * @param request Solicitud HTTP que se está procesando.
     * @return ResponseEntity que contiene la lista de tarjetas y los enlaces de paginación.
     */
    @Operation(
            summary = "Listar tarjetas bancarias",
            description = "Obtiene una lista paginada de tarjetas filtradas por los parámetros proporcionados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de tarjetas obtenida exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            }
    )
    @GetMapping
    public ResponseEntity<PageResponse<TarjetaResponse>> getAll(
            @RequestParam(required = false) Optional<String> numero,
            @RequestParam(required = false) Optional<LocalDate> caducidad,
            @RequestParam(required = false) Optional<TipoTarjeta> tipoTarjeta,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteDiario,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteDiario,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteSemanal,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteSemanal,
            @RequestParam(required = false) Optional<BigDecimal> minLimiteMensual,
            @RequestParam(required = false) Optional<BigDecimal> maxLimiteMensual,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Recibiendo solicitud para listar tarjetas con filtros proporcionados");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<TarjetaResponse> pageResult = tarjetaService.getAll(numero, caducidad, tipoTarjeta, minLimiteDiario, maxLimiteDiario, minLimiteSemanal, maxLimiteSemanal, minLimiteMensual, maxLimiteMensual, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene una tarjeta por su identificador.
     *
     * @param id Identificador de la tarjeta
     * @return Respuesta con los detalles de la tarjeta correspondiente
     */
    @Operation(
            summary = "Obtener tarjeta por ID",
            description = "Recupera una tarjeta utilizando su identificador único.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjeta obtenida exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @GetMapping("{id}")
    public ResponseEntity<TarjetaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(tarjetaService.getById(id));
    }

    /**
     * Obtiene los datos privados de una tarjeta por su id.
     *
     * <p>Requiere autenticación de usuario y la contraseña de la tarjeta.</p>
     *
     * @param id Identificador de la tarjeta
     * @param tarjetaRequestPrivado Contraseña de la tarjeta
     * @return Respuesta con los datos privados de la tarjeta
     */
    @Operation(
            summary = "Obtener datos privados de una tarjeta",
            description = "Recupera los datos privados de una tarjeta utilizando su identificadorático y la contraseña.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Datos privados obtenidos exitosamente"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @GetMapping("{id}/private")
    public ResponseEntity<TarjetaResponsePrivado> getPrivateData(@PathVariable String id, @RequestBody TarjetaRequestPrivado tarjetaRequestPrivado) {
        return ResponseEntity.ok(tarjetaService.getPrivateData(id, tarjetaRequestPrivado));
    }

    /**
     * Guarda una nueva tarjeta en el sistema.
     *
     * @param tarjetaRequestSave Datos de la tarjeta a guardar
     * @return Respuesta con los detalles de la tarjeta creada
     */
    @Operation(
            summary = "Crear tarjeta",
            description = "Crea una nueva tarjeta en el sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarjeta creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado")
            }
    )
    @PostMapping
    public ResponseEntity<TarjetaResponse> save(@Valid @RequestBody TarjetaRequestSave tarjetaRequestSave) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjetaService.save(tarjetaRequestSave));
    }

    /**
     * Actualiza una tarjeta existente.
     *
     * @param id Identificador de la tarjeta a actualizar
     * @param tarjetaRequestUpdate Datos actualizados de la tarjeta
     * @return Tarjeta actualizada
     */
    @Operation(
            summary = "Actualizar tarjeta",
            description = "Actualiza los datos de una tarjeta existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjeta actualizada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos", content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @PutMapping("{id}")
    public ResponseEntity<TarjetaResponse> update(@PathVariable String id, @Valid @RequestBody TarjetaRequestUpdate tarjetaRequestUpdate) {
        return ResponseEntity.ok(tarjetaService.update(id, tarjetaRequestUpdate));
    }
    
    /**
     * Elimina una tarjeta por su identificador.
     *
     * @param id Identificador de la tarjeta a eliminar
     * @return Respuesta sin contenido
     */
    @Operation(
            summary = "Eliminar tarjeta",
            description = "Elimina una tarjeta utilizando su identificador único.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarjeta eliminada exitosamente"),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @DeleteMapping("{id}")
    public ResponseEntity<TarjetaResponse> delete(@PathVariable String id) {
        log.info("Tarjeta borrada con id: {}", id);
        tarjetaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Maneja las excepciones de validación para solicitudes incorrectas.
     * Captura errores de validación de argumentos y restricciones.
     *
     * @param ex Excepción de validación producida
     * @return Mapa de errores de validación
     */
    @Operation(hidden = true)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Map<String, String> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach((error) -> {
                String fieldName = error.getField();
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