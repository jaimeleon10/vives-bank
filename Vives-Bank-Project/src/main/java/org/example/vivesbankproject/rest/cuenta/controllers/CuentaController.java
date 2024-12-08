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
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador para la gestión de cuentas del tipo RestController
 * Fijamos la ruta de acceso a este controlador.
 * Utiliza el servicio de cuentas y herramientas de paginación inyectadas a través del constructor.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("${api.version}/cuentas")
@Slf4j
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class CuentaController {
    private final CuentaService cuentaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CuentaController(CuentaService cuentaService, PaginationLinksUtils paginationLinksUtils) {
        this.cuentaService = cuentaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Obtiene todas las cuentas con opciones de filtrado y paginación.
     *
     * @param iban       Filtra por IBAN de la cuenta
     * @param saldoMax   Filtra por saldo máximo
     * @param saldoMin   Filtra por saldo mínimo
     * @param tipoCuenta Filtra por tipo de cuenta
     * @param page       Número de página (por defecto: 0)
     * @param size       Tamaño de la página (por defecto: 10)
     * @param sortBy     Campo para ordenar (por defecto: id)
     * @param direction  Dirección de orden (ascendente o descendente, por defecto: asc)
     * @param request    Solicitud HTTP
     * @return Página de cuentas filtradas
     */
    @Operation(summary = "Obtiene todas las cuentas", description = "Devuelve una lista paginada de cuentas con filtros opcionales.")
    @Parameters({
            @Parameter(name = "iban", description = "IBAN de la cuenta", example = "ES12345678901234567890123456"),
            @Parameter(name = "saldoMax", description = "Saldo máximo", example = "1000"),
            @Parameter(name = "saldoMin", description = "Saldo mínimo", example = "100"),
            @Parameter(name = "tipoCuenta", description = "Tipo de cuenta", example = "AHORROS"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de cuentas")
    })

    @GetMapping()
    public ResponseEntity<PageResponse<CuentaResponse>> getAll(
            @RequestParam(required = false) Optional<String> iban,
            @RequestParam(required = false) Optional<BigDecimal> saldoMax,
            @RequestParam(required = false) Optional<BigDecimal> saldoMin,
            @RequestParam(required = false) Optional<String> tipoCuenta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ){
        log.info("Buscando todas las cuentas con las siguientes opciones: {}, {}, {}, {}", iban, saldoMax, saldoMin, tipoCuenta);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<CuentaResponse> pageResult = cuentaService.getAll(iban, saldoMax, saldoMin, tipoCuenta, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Obtiene todas las cuentas asociadas a un cliente por su identificador.
     *
     * @param clienteGuid Identificador del cliente
     * @return Lista de respuestas con las cuentas del cliente
     */
    @Operation(summary = "Obtiene todas las cuentas de un cliente por su identificador", description = "Devuelve todas las cuentas de un cliente específico.")

    @GetMapping("cliente/{clienteGuid}")
    public ResponseEntity<ArrayList<CuentaResponse>> getAllCuentasByClienteGuid(@PathVariable String clienteGuid) {
        return ResponseEntity.ok(cuentaService.getAllCuentasByClienteGuid(clienteGuid));
    }

    /**
     * Obtiene una cuenta por su identificador único.
     *
     * @param id Identificador de la cuenta
     * @return Cuenta encontrada
     */
    @Operation(summary = "Obtiene una cuenta por su identificador", description = "Devuelve la información de una cuenta específica.")
    @GetMapping("{id}")
    public ResponseEntity<CuentaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(cuentaService.getById(id));
    }

    /**
     * Obtiene una cuenta por su IBAN.
     *
     * @param iban IBAN de la cuenta
     * @return Cuenta encontrada
     */
    @Operation(summary = "Obtiene una cuenta por IBAN", description = "Devuelve la información de una cuenta usando su IBAN.")
    @GetMapping("/iban/{iban}")
    public ResponseEntity<CuentaResponse> getByIban(@PathVariable String iban) {
        return ResponseEntity.ok(cuentaService.getByIban(iban));
    }
    /**
     * Crea una nueva cuenta.
     *
     * @param cuentaRequest Datos de la cuenta a crear
     * @return Cuenta creada
     */
    @Operation(summary = "Crea una cuenta", description = "Crea una nueva cuenta en la base de datos.")
    @PostMapping
    public ResponseEntity<CuentaResponse> save(@Valid @RequestBody CuentaRequest cuentaRequest) {
        var result = cuentaService.save(cuentaRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    /**
     * Actualiza una cuenta existente.
     *
     * @param id                  Identificador de la cuenta
     * @param cuentaRequestUpdate Datos actualizados de la cuenta
     * @return Cuenta actualizada
     */
    @Operation(summary = "Actualiza una cuenta", description = "Actualiza los detalles de una cuenta existente en la base de datos.")
    @PutMapping("{id}")
    public ResponseEntity<CuentaResponse> update(@PathVariable String id, @Valid @RequestBody CuentaRequestUpdate cuentaRequestUpdate) {
        var result = cuentaService.update(id, cuentaRequestUpdate);
        return ResponseEntity.ok(result);
    }
    /**
     * Elimina una cuenta por su identificador.
     *
     * @param id Identificador de la cuenta
     * @return Respuesta sin contenido
     */
    @Operation(summary = "Elimina una cuenta", description = "Elimina una cuenta de la base de datos a partir de su identificador.")
    @PatchMapping("{id}")
    public ResponseEntity<Cuenta> delete(@PathVariable String id) {
        cuentaService.deleteById(id);
        return ResponseEntity.noContent().build();
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