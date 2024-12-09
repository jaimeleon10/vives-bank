package org.example.vivesbankproject.rest.movimientos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.rest.movimientos.models.Transferencia;
import org.example.vivesbankproject.rest.movimientos.services.MovimientosService;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
/**
 * Controlador para la gestión de operaciones personales de movimientos. Proporciona endpoints para realizar
 * operaciones específicas para el usuario autenticado como movimientos de domiciliación, ingreso de nómina, pagos con tarjeta,
 * transferencias y revocación de transferencias.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@RestController
@RequestMapping("${api.version}/me")
@Slf4j
@PreAuthorize("hasRole('USER')")

public class MovimientosMeController {

    private final MovimientosService movimientosService;

    /**
     * Constructor para inyectar la dependencia de servicio.
     *
     * @param movimientosService Servicio para la lógica de negocio relacionada con movimientos personales.
     */
    @Autowired
    public MovimientosMeController(MovimientosService movimientosService) {
        this.movimientosService = movimientosService;
    }

    /**
     * Crea un movimiento de domiciliación para el usuario autenticado.
     *
     * @param user    El usuario autenticado.
     * @param request Objeto Domiciliacion con la información de la solicitud.
     * @return ResponseEntity con el movimiento de domiciliación creado.
     */
    @Operation(
            summary = "Crear movimiento de domiciliación",
            description = "Crea un nuevo movimiento de domiciliación para el usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento de domiciliación creado con éxito",
                    content = @Content(schema = @Schema(implementation = Domiciliacion.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/domiciliacion")
    public ResponseEntity<Domiciliacion> createMovimientoDomiciliacion(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid Domiciliacion request
    ) {
        log.info("Creando Movimiento de Domiciliacion");
        return ResponseEntity.ok(movimientosService.saveDomiciliacion(user, request));
    }

    /**
     * Crea un movimiento de ingreso de nómina para el usuario autenticado.
     *
     * @param user    El usuario autenticado.
     * @param request Objeto IngresoDeNomina con la información de la solicitud.
     * @return ResponseEntity con el movimiento de ingreso de nómina creado.
     */
    @Operation(
            summary = "Crear movimiento de ingreso de nómina",
            description = "Crea un nuevo movimiento de ingreso de nómina para el usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento de ingreso de nómina creado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/ingresonomina")
    public ResponseEntity<MovimientoResponse> createMovimientoIngresoNomina(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid IngresoDeNomina request
    ) {
        log.info("Creando Movimiento de Ingreso de Nomina");
        return ResponseEntity.ok(movimientosService.saveIngresoDeNomina(user, request));
    }

    /**
     * Crea un movimiento de pago con tarjeta para el usuario autenticado.
     *
     * @param user    El usuario autenticado.
     * @param request Objeto PagoConTarjeta con la información de la solicitud.
     * @return ResponseEntity con el movimiento de pago con tarjeta creado.
     */
    @Operation(
            summary = "Crear movimiento de pago con tarjeta",
            description = "Crea un nuevo movimiento de pago con tarjeta para el usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento de pago con tarjeta creado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/pagotarjeta")
    public ResponseEntity<MovimientoResponse> createMovimientoPagoConTarjeta(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PagoConTarjeta request
    ) {
        log.info("Creando Movimiento de Pago con Tarjeta");
        return ResponseEntity.ok(movimientosService.savePagoConTarjeta(user, request));
    }

    /**
     * Crea una transferencia para el usuario autenticado.
     *
     * @param user    El usuario autenticado.
     * @param request Objeto Transferencia con la información de la solicitud.
     * @return ResponseEntity con el movimiento de transferencia creado.
     */
    @Operation(
            summary = "Crear movimiento de transferencia",
            description = "Crea un nuevo movimiento de transferencia para el usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento de transferencia creado con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/transferencia")
    public ResponseEntity<MovimientoResponse> createMovimientoTransferencia(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid Transferencia request
    ) {
        log.info("Creando Movimiento de Transferencia");
        return ResponseEntity.ok(movimientosService.saveTransferencia(user, request));
    }

    /**
     * Revoca una transferencia para el usuario autenticado.
     *
     * @param user El usuario autenticado.
     * @param guid Identificador único de la transferencia a revocar.
     * @return ResponseEntity con la transferencia revocada.
     */
    @Operation(
            summary = "Revocar movimiento de transferencia",
            description = "Revoca una transferencia específica para el usuario autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia revocada con éxito",
                    content = @Content(schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transferencia no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/transferencia/{guid}")
    public ResponseEntity<MovimientoResponse> revocarMovimientoTransferencia(
            @AuthenticationPrincipal User user,
            @PathVariable String guid
    ) {
        log.info("Revocando Movimiento de Transferencia con guid: {}", guid);
        return ResponseEntity.ok(movimientosService.revocarTransferencia(user, guid));
    }

    /**
     * Manejador de excepciones para validaciones en el cuerpo de la solicitud.
     *
     * @param ex La excepción capturada durante la validación.
     * @return Mapa con los errores de validación.
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
        }
        return errors;
    }
}