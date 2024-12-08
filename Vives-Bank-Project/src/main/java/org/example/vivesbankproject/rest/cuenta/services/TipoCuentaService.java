package org.example.vivesbankproject.rest.cuenta.services;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Servicio para realizar operaciones relacionadas con el tipo de cuenta en la aplicación.
 * Proporciona métodos para obtener, actualizar, eliminar y crear información relacionada con los tipos de cuentas.
 * Se utiliza para definir la lógica de negocio entre el repositorio y el controlador.
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
public interface TipoCuentaService {

    /**
     * Obtiene todas las entradas de tipo cuenta con filtros opcionales aplicados (por nombre, interés mínimo, interés máximo).
     *
     * @param nombre Filtro opcional por nombre del tipo de cuenta
     * @param interesMax Interés máximo para el filtro (opcional)
     * @param interesMin Interés mínimo para el filtro (opcional)
     * @param pageable Parámetros de paginación para la consulta
     * @return Página de respuestas con información de los tipos de cuenta
     */
    @Operation(summary = "Obtener todas las cuentas con filtros opcionales",
            description = "Devuelve una lista paginada de respuestas con información de los tipos de cuenta aplicando filtros opcionales.")
    @Parameter(name = "nombre", description = "Filtro opcional por el nombre del tipo de cuenta", required = false)
    @Parameter(name = "interesMax", description = "Filtro opcional para el interés máximo", required = false)
    @Parameter(name = "interesMin", description = "Filtro opcional para el interés mínimo", required = false)
    Page<TipoCuentaResponse> getAll(
            Optional<String> nombre,
            Optional<BigDecimal> interesMax,
            Optional<BigDecimal> interesMin,
            Pageable pageable);

    /**
     * Obtiene un tipo de cuenta específico por su identificador.
     *
     * @param id Identificador único del tipo de cuenta
     * @return Respuesta con información del tipo de cuenta encontrado
     */
    @Operation(summary = "Obtener un tipo de cuenta por su identificador",
            description = "Devuelve una respuesta con información del tipo de cuenta utilizando su identificador.")
    @Parameter(name = "id", description = "Identificador único del tipo de cuenta", required = true)
    TipoCuentaResponse getById(String id);

    /**
     * Guarda un nuevo tipo de cuenta en el sistema utilizando la información proporcionada.
     *
     * @param tipoCuentaRequest Información para crear un nuevo tipo de cuenta
     * @return Respuesta con información del tipo de cuenta creado
     */
    @Operation(summary = "Guardar un nuevo tipo de cuenta",
            description = "Crea un nuevo tipo de cuenta utilizando la información proporcionada en el objeto tipoCuentaRequest.")
    @Parameter(name = "tipoCuentaRequest", description = "Información para crear un nuevo tipo de cuenta", required = true)
    TipoCuentaResponse save(TipoCuentaRequest tipoCuentaRequest);

    /**
     * Actualiza un tipo de cuenta existente en el sistema utilizando la información proporcionada.
     *
     * @param id Identificador del tipo de cuenta a actualizar
     * @param tipoCuentaRequest Información para actualizar el tipo de cuenta
     * @return Respuesta con información del tipo de cuenta actualizado
     */
    @Operation(summary = "Actualizar un tipo de cuenta existente",
            description = "Actualiza un tipo de cuenta específico utilizando la información proporcionada en el objeto tipoCuentaRequest.")
    @Parameter(name = "id", description = "Identificador del tipo de cuenta a actualizar", required = true)
    @Parameter(name = "tipoCuentaRequest", description = "Información para actualizar el tipo de cuenta", required = true)
    TipoCuentaResponse update(String id, TipoCuentaRequest tipoCuentaRequest);

    /**
     * Elimina un tipo de cuenta por su identificador.
     *
     * @param id Identificador del tipo de cuenta a eliminar
     * @return Respuesta con información del tipo de cuenta eliminado
     */
    @Operation(summary = "Eliminar un tipo de cuenta por su identificador",
            description = "Elimina un tipo de cuenta del sistema utilizando su identificador.")
    @Parameter(name = "id", description = "Identificador del tipo de cuenta a eliminar", required = true)
    TipoCuentaResponse deleteById(String id);
}