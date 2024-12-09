package org.example.vivesbankproject.rest.movimientos.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.bson.types.ObjectId;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.rest.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.rest.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.rest.movimientos.models.Transferencia;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * Interfaz de servicio para la lógica de negocios relacionada con movimientos.
 * Contiene métodos para obtener, guardar y manejar operaciones relacionadas
 * con movimientos, domiciliaciones, pagos, ingresos de nómina, y transferencias.
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
public interface MovimientosService {

    /**
     * Obtiene una lista paginada de todos los movimientos.
     *
     * @param pageable Información para la paginación de resultados.
     * @return Una página de respuestas de tipo MovimientoResponse.
     */
    @Operation(summary = "Obtener todos los movimientos paginados", description = "Retorna una lista paginada de todos los movimientos en formato MovimientoResponse")
    Page<MovimientoResponse> getAll(Pageable pageable);

    /**
     * Obtiene un movimiento específico utilizando su identificador interno (_id).
     *
     * @param _id Identificador interno de la base de datos.
     * @return El movimiento correspondiente a ese identificador en formato MovimientoResponse.
     */
    @Operation(summary = "Obtener un movimiento por su ID", description = "Retorna un movimiento específico por su identificador interno (_id)")
    MovimientoResponse getById(ObjectId _id);

    /**
     * Obtiene un movimiento específico utilizando su GUID.
     *
     * @param guidMovimiento El GUID que identifica el movimiento.
     * @return El movimiento correspondiente en formato MovimientoResponse.
     */
    @Operation(summary = "Obtener un movimiento por su GUID", description = "Retorna un movimiento específico utilizando su identificador GUID")
    MovimientoResponse getByGuid(String guidMovimiento);

    /**
     * Obtiene un movimiento específico utilizando el identificador del cliente.
     *
     * @param idCliente El identificador del cliente asociado al movimiento.
     * @return El movimiento correspondiente en formato MovimientoResponse.
     */
    @Operation(summary = "Obtener un movimiento por el GUID del cliente", description = "Retorna un movimiento específico utilizando el identificador del cliente")
    MovimientoResponse getByClienteGuid(String idCliente);

    /**
     * Guarda un nuevo movimiento en la base de datos.
     *
     * @param movimientoRequest Datos necesarios para guardar el movimiento.
     * @return El objeto MovimientoResponse guardado en la base de datos.
     */
    @Operation(summary = "Guardar un nuevo movimiento", description = "Crea un nuevo movimiento en la base de datos con la información proporcionada")
    MovimientoResponse save(@RequestBody MovimientoRequest movimientoRequest);

    /**
     * Guarda una domiciliación.
     *
     * @param user Usuario autenticado.
     * @param domiciliacion Datos de la domiciliación.
     * @return La domiciliación guardada.
     */
    @Operation(summary = "Guardar una nueva domiciliación", description = "Crea una nueva domiciliación en la base de datos")
    Domiciliacion saveDomiciliacion(@AuthenticationPrincipal User user, @RequestBody Domiciliacion domiciliacion);

    /**
     * Guarda un nuevo ingreso de nómina.
     *
     * @param user Usuario autenticado.
     * @param ingresoDeNomina Información sobre el ingreso de nómina.
     * @return El ingreso de nómina guardado en formato MovimientoResponse.
     */
    @Operation(summary = "Guardar un ingreso de nómina", description = "Crea un nuevo registro de ingreso de nómina")
    MovimientoResponse saveIngresoDeNomina(@AuthenticationPrincipal User user, @RequestBody IngresoDeNomina ingresoDeNomina);

    /**
     * Guarda un nuevo pago con tarjeta.
     *
     * @param user Usuario autenticado.
     * @param pagoConTarjeta Datos del pago con tarjeta.
     * @return El pago con tarjeta guardado en formato MovimientoResponse.
     */
    @Operation(summary = "Guardar un pago con tarjeta", description = "Crea un nuevo pago con tarjeta en la base de datos")
    MovimientoResponse savePagoConTarjeta(@AuthenticationPrincipal User user, @RequestBody PagoConTarjeta pagoConTarjeta);

    /**
     * Guarda una nueva transferencia.
     *
     * @param user Usuario autenticado.
     * @param transferencia Información de la transferencia.
     * @return La transferencia guardada en formato MovimientoResponse.
     */
    @Operation(summary = "Guardar una transferencia", description = "Crea una nueva transferencia en la base de datos")
    MovimientoResponse saveTransferencia(@AuthenticationPrincipal User user, @RequestBody Transferencia transferencia);

    /**
     * Revoca una transferencia específica.
     *
     * @param user Usuario autenticado.
     * @param movimientoTransferenciaGuid El identificador de la transferencia a revocar.
     * @return La información de la transferencia revocada en formato MovimientoResponse.
     */
    @Operation(summary = "Revocar una transferencia", description = "Revoca una transferencia específica basada en su identificador")
    MovimientoResponse revocarTransferencia(@AuthenticationPrincipal User user, String movimientoTransferenciaGuid);
}