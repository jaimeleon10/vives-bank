package org.example.vivesbankproject.movimientos.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.movimientos.models.Transferencia;
import org.example.vivesbankproject.movimientos.services.MovimientosService;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/ingresonomina/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoIngresoNomina(@AuthenticationPrincipal User user, @RequestBody @Valid IngresoDeNomina request) {

        return null;
    }

    @PostMapping("/pagotarjeta/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoPagoConTarjeta(@AuthenticationPrincipal User user, @RequestBody @Valid PagoConTarjeta request) {

        return null;
    }

    @PostMapping("/transferencia/{iban}")
    public ResponseEntity<MovimientoResponse> createMovimientoTransferencia(@AuthenticationPrincipal User user, @PathVariable String iban , @RequestBody @Valid Transferencia request) {

        return null;
    }

    @DeleteMapping("/transferencia/{guid}")
    public ResponseEntity<MovimientoResponse> revocarMovimientoTransferencia(@AuthenticationPrincipal User user, @PathVariable String guid) {

        return null;
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
