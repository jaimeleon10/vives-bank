package org.example.vivesbankproject.rest.cuenta.mappers;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.models.Cuenta;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.tarjeta.models.Tarjeta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Componente que realiza la conversión entre entidades de dominio y objetos de transferencia
 * relacionados con las cuentas bancarias.
 *
 * <p>Este mapper facilita la conversión de datos para operaciones CRUD en las cuentas,
 * manejando tanto las conversiones de entidades a respuestas, como las de solicitudes a entidades.</p>
 *
 * @version 1.0-SNAPSHOT
 * @see CuentaResponse
 * @see Cuenta
 * @see CuentaRequestUpdate
 * @see TipoCuenta
 * @see Tarjeta
 * @see Cliente
 * @see Component
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 */
@Component
public class CuentaMapper {

    /**
     * Convierte una entidad {@link Cuenta} junto con los identificadores relacionados a un objeto {@link CuentaResponse}.
     *
     * @param cuenta        Entidad {@link Cuenta} a convertir
     * @param tipoCuentaId  Identificador del tipo de cuenta relacionado
     * @param tarjetaId     Identificador de la tarjeta relacionada
     * @param clienteId     Identificador del cliente relacionado
     * @return Un objeto {@link CuentaResponse} que representa la cuenta y sus relaciones
     */
    @Schema(description = "Convierte una entidad Cuenta a un objeto de respuesta CuentaResponse")
    public CuentaResponse toCuentaResponse(Cuenta cuenta, String tipoCuentaId, String tarjetaId, String clienteId) {
        return CuentaResponse.builder()
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuenta.getSaldo().toString())
                .tipoCuentaId(tipoCuentaId)
                .tarjetaId(tarjetaId)
                .clienteId(clienteId)
                .createdAt(cuenta.getCreatedAt().toString())
                .updatedAt(cuenta.getUpdatedAt().toString())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    /**
     * Convierte los objetos relacionados en una nueva instancia de {@link Cuenta}.
     *
     * @param tipoCuenta Objeto {@link TipoCuenta} asociado a la cuenta
     * @param tarjeta    Objeto {@link Tarjeta} asociado a la cuenta
     * @param cliente    Objeto {@link Cliente} asociado a la cuenta
     * @return Una nueva entidad {@link Cuenta} creada a partir de las relaciones proporcionadas
     */
    @Schema(description = "Convierte objetos relacionados a una nueva entidad de tipo Cuenta")
    public Cuenta toCuenta(TipoCuenta tipoCuenta, Tarjeta tarjeta, Cliente cliente) {
        return Cuenta.builder()
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .cliente(cliente)
                .build();
    }

    /**
     * Actualiza una instancia existente de {@link Cuenta} con datos proporcionados en {@link CuentaRequestUpdate}.
     *
     * @param cuentaRequestUpdate Objeto {@link CuentaRequestUpdate} con los nuevos datos de la cuenta
     * @param cuenta              Instancia existente de {@link Cuenta} a actualizar
     * @param tipoCuenta          Objeto {@link TipoCuenta} asociado
     * @param tarjeta             Objeto {@link Tarjeta} asociado
     * @param cliente             Objeto {@link Cliente} asociado
     * @return Una nueva instancia de {@link Cuenta} con los datos actualizados
     */
    @Schema(description = "Actualiza una cuenta existente con los datos proporcionados")
    public Cuenta toCuentaUpdate(CuentaRequestUpdate cuentaRequestUpdate, Cuenta cuenta, TipoCuenta tipoCuenta, Tarjeta tarjeta, Cliente cliente) {
        return Cuenta.builder()
                .id(cuenta.getId())
                .guid(cuenta.getGuid())
                .iban(cuenta.getIban())
                .saldo(cuentaRequestUpdate.getSaldo())
                .tipoCuenta(tipoCuenta)
                .tarjeta(tarjeta)
                .cliente(cliente)
                .createdAt(cuenta.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }

    /**
     * Convierte un objeto {@link CuentaResponse} en una solicitud de actualización {@link CuentaRequestUpdate}.
     *
     * @param cuenta Objeto {@link CuentaResponse} a convertir
     * @return Un objeto {@link CuentaRequestUpdate} con los datos de la respuesta
     */
    @Schema(description = "Convierte una respuesta CuentaResponse en una solicitud de actualización CuentaRequestUpdate")
    public CuentaRequestUpdate toCuentaRequestUpdate(CuentaResponse cuenta) {
        return CuentaRequestUpdate.builder()
                .saldo(new BigDecimal(cuenta.getSaldo()))
                .tipoCuentaId(cuenta.getTipoCuentaId())
                .tarjetaId(cuenta.getTarjetaId())
                .clienteId(cuenta.getClienteId())
                .isDeleted(cuenta.getIsDeleted())
                .build();
    }
}