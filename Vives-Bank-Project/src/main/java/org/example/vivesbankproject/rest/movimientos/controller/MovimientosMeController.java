package org.example.vivesbankproject.rest.movimientos.controller;

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

@RestController
@RequestMapping("${api.version}/me")
@Slf4j
@PreAuthorize("hasRole('USER')")
public class MovimientosMeController {

    private final MovimientosService movimientosService;

    @Autowired
    public MovimientosMeController(MovimientosService movimientosService) {
        this.movimientosService = movimientosService;
    }

    @PostMapping("/domiciliacion")
    public ResponseEntity<Domiciliacion> createMovimientoDomiciliacion(@AuthenticationPrincipal User user, @RequestBody @Valid Domiciliacion request) {
        log.info("Creando Movimiento de Domiciliacion");
        return ResponseEntity.ok(movimientosService.saveDomiciliacion(user, request));
    }

    @PostMapping("/ingresonomina")
    public ResponseEntity<MovimientoResponse> createMovimientoIngresoNomina(@AuthenticationPrincipal User user, @RequestBody @Valid IngresoDeNomina request) {
        log.info("Creando Movimiento de Ingreso de Nomina");
        return ResponseEntity.ok(movimientosService.saveIngresoDeNomina(user, request));
    }

    @PostMapping("/pagotarjeta")
    public ResponseEntity<MovimientoResponse> createMovimientoPagoConTarjeta(@AuthenticationPrincipal User user, @RequestBody @Valid PagoConTarjeta request) {
        log.info("Creando Movimiento de Pago con Tarjeta");
        return ResponseEntity.ok(movimientosService.savePagoConTarjeta(user, request));
    }

    @PostMapping("/transferencia")
    public ResponseEntity<MovimientoResponse> createMovimientoTransferencia(@AuthenticationPrincipal User user, @RequestBody @Valid Transferencia request) {
        log.info("Creando Movimiento de Transferencia");
        return ResponseEntity.ok(movimientosService.saveTransferencia(user, request));
    }

    @DeleteMapping("/transferencia/{guid}")
    public ResponseEntity<MovimientoResponse> revocarMovimientoTransferencia(@AuthenticationPrincipal User user, @PathVariable String guid) {
        log.info("Revocando Movimiento de Transferencia con guid: {}", guid);
        return ResponseEntity.ok(movimientosService.revocarTransferencia(user, guid));
    }

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
