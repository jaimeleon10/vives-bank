package org.example.vivesbankproject.cuenta.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Servicio para realizar operaciones sobre cuentas en la aplicación.
 * Proporciona métodos para obtener, actualizar, eliminar y crear información relacionada con cuentas.
 * Se utiliza para definir la lógica de negocio entre el repositorio y el controlador.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
public interface CuentaService {

    /**
     * Obtiene todas las cuentas con filtros opcionales aplicados (por IBAN, saldo mínimo, saldo máximo y tipo de cuenta).
     *
     * @param iban Filtro por IBAN (opcional)
     * @param saldoMax Saldo máximo para el filtro (opcional)
     * @param saldoMin Saldo mínimo para el filtro (opcional)
     * @param tipoCuenta Filtro por tipo de cuenta (opcional)
     * @param pageable Parámetros de paginación para la consulta
     * @return Página de respuestas con información de cuentas
     */
    @Operation(summary = "Obtener todas las cuentas con filtros opcionales",
            description = "Devuelve una lista paginada de respuestas con información de cuentas aplicando filtros opcionales.")
    @Parameter(name = "iban", description = "Filtro opcional por IBAN", required = false)
    @Parameter(name = "saldoMax", description = "Filtro opcional para el saldo máximo", required = false)
    @Parameter(name = "saldoMin", description = "Filtro opcional para el saldo mínimo", required = false)
    @Parameter(name = "tipoCuenta", description = "Filtro opcional por tipo de cuenta", required = false)
    Page<CuentaResponse> getAll(
            Optional<String> iban,
            Optional<BigDecimal> saldoMax,
            Optional<BigDecimal> saldoMin,
            Optional<String> tipoCuenta,
            Pageable pageable);

    /**
     * Obtiene todas las cuentas asociadas a un identificador de cliente específico.
     *
     * @param clienteGuid Identificador global único del cliente para el filtro
     * @return Lista de respuestas con información de las cuentas asociadas
     */
    @Operation(summary = "Obtener todas las cuentas por cliente GUID",
            description = "Devuelve una lista de todas las cuentas asociadas al GUID del cliente proporcionado.")
    @Parameter(name = "clienteGuid", description = "Identificador global único del cliente para el filtro", required = true)
    ArrayList<CuentaResponse> getAllCuentasByClienteGuid(String clienteGuid);

    /**
     * Obtiene una cuenta específica por su identificador.
     *
     * @param id Identificador único de la cuenta
     * @return Respuesta con información de la cuenta encontrada
     */
    @Operation(summary = "Obtener una cuenta por su identificador",
            description = "Devuelve una respuesta con información de la cuenta utilizando su identificador.")
    @Parameter(name = "id", description = "Identificador único de la cuenta", required = true)
    CuentaResponse getById(String id);

    /**
     * Obtiene una cuenta específica por su IBAN.
     *
     * @param iban Número IBAN de la cuenta a buscar
     * @return Respuesta con información de la cuenta encontrada
     */
    @Operation(summary = "Obtener una cuenta por su IBAN",
            description = "Devuelve una respuesta con información de la cuenta utilizando el IBAN proporcionado.")
    @Parameter(name = "iban", description = "Número IBAN para realizar la búsqueda", required = true)
    CuentaResponse getByIban(String iban);

    /**
     * Guarda una nueva cuenta en el sistema utilizando la información proporcionada.
     *
     * @param cuentaRequest Información para crear la cuenta
     * @return Respuesta con información de la cuenta creada
     */
    @Operation(summary = "Guardar una nueva cuenta",
            description = "Crea una nueva cuenta utilizando la información proporcionada en el objeto cuentaRequest.")
    @Parameter(name = "cuentaRequest", description = "Información para crear una cuenta", required = true)
    CuentaResponse save(CuentaRequest cuentaRequest);

    /**
     * Actualiza una cuenta existente en el sistema utilizando la información proporcionada.
     *
     * @param id Identificador de la cuenta a actualizar
     * @param cuentaRequestUpdate Información de actualización para la cuenta
     * @return Respuesta con información de la cuenta actualizada
     */
    @Operation(summary = "Actualizar una cuenta existente",
            description = "Actualiza una cuenta específica utilizando la información proporcionada en el objeto cuentaRequestUpdate.")
    @Parameter(name = "id", description = "Identificador de la cuenta a actualizar", required = true)
    @Parameter(name = "cuentaRequestUpdate", description = "Información para actualizar la cuenta", required = true)
    CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate);

    /**
     * Elimina una cuenta por su identificador.
     *
     * @param id Identificador de la cuenta a eliminar
     */
    @Operation(summary = "Eliminar una cuenta por su identificador",
            description = "Elimina una cuenta del sistema utilizando su identificador.")
    @Parameter(name = "id", description = "Identificador de la cuenta a eliminar", required = true)
    void deleteById(String id);
}