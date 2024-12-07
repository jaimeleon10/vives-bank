package org.example.vivesbankproject.cuenta.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.criteria.Join;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByIban;
import org.example.vivesbankproject.cuenta.exceptions.cuenta.CuentaNotFoundByNumTarjeta;
import org.example.vivesbankproject.cuenta.exceptions.tipoCuenta.TipoCuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFound;
import org.example.vivesbankproject.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketConfig;
import org.example.vivesbankproject.websocket.notifications.config.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Servicio de implementación para el manejo de cuentas.
 * <p>
 * Este servicio permite realizar operaciones CRUD sobre las cuentas, así como buscar
 * información mediante filtros específicos.
 * </p>
 *
 * @author Jaime León, Natalia González,
 *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Service
@Slf4j
@CacheConfig(cacheNames = {"cuenta"})
public class CuentaServiceImpl implements CuentaService{
    private final CuentaRepository cuentaRepository;
    private final CuentaMapper cuentaMapper;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;

    private final UserRepository userRepository;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    // Para los test
    @Setter
    private WebSocketHandler webSocketService;

    @Autowired
    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper, TipoCuentaRepository tipoCuentaRepository, TarjetaRepository tarjetaRepository, ClienteRepository clienteRepository, WebSocketConfig webSocketConfig, UserRepository userRepository) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.clienteRepository = clienteRepository;

        this.userRepository = userRepository;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketTarjetasHandler();
        mapper = new ObjectMapper();
    }

    /**
     * Obtiene todas las cuentas con filtros opcionales.
     *
     * @param iban        El IBAN de la cuenta.
     * @param saldoMax    Saldo máximo para el filtro.
     * @param saldoMin    Saldo mínimo para el filtro.
     * @param tipoCuenta El tipo de cuenta para el filtro.
     * @param pageable   La paginación de la consulta.
     * @return Devuelve una página de respuestas con la información de las cuentas.
     */
    @Override
    @Operation(summary = "Obtener todas las cuentas con filtros", description = "Devuelve una lista paginada de cuentas con filtros opcionales.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devuelve una lista de cuentas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno en el servidor")
    })
    public Page<CuentaResponse> getAll(Optional<String> iban, Optional<BigDecimal> saldoMax, Optional<BigDecimal> saldoMin, Optional<String> tipoCuenta, Pageable pageable) {
        log.info("Obteniendo todas las cuentas");

        Specification<Cuenta> specIbanCuenta = (root, query, criteriaBuilder) ->
                iban.map(i -> criteriaBuilder.like(criteriaBuilder.lower(root.get("iban")), "%" + i.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMaxCuenta = (root, query, criteriaBuilder) ->
                saldoMax.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specSaldoMinCuenta = (root, query, criteriaBuilder) ->
                saldoMin.map(s -> criteriaBuilder.greaterThanOrEqualTo(root.get("saldo"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> specTipoCuenta = (root, query, criteriaBuilder) ->
                tipoCuenta.map(t -> {
                    Join<Cuenta, TipoCuenta> tipoCuentaJoin = root.join("tipoCuenta");
                    return criteriaBuilder.like(criteriaBuilder.lower(tipoCuentaJoin.get("nombre")), "%" + t.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cuenta> criterio = Specification.where(specIbanCuenta)
                .and(specSaldoMaxCuenta)
                .and(specSaldoMinCuenta)
                .and(specTipoCuenta);

        Page<Cuenta> cuentaPage = cuentaRepository.findAll(criterio, pageable);

        return cuentaPage.map(cuenta ->
                cuentaMapper.toCuentaResponse(
                        cuenta,
                        cuenta.getTipoCuenta().getGuid(),
                        cuenta.getTarjeta().getGuid(),
                        cuenta.getCliente().getGuid()
                )
        );
    }

    /**
     * Recupera todas las cuentas asociadas a un cliente específico utilizando su identificador GUID.
     * Esta operación consulta la base de datos para obtener todas las cuentas relacionadas con el GUID del cliente.
     *
     * @param clienteGuid El identificador único (GUID) del cliente para el que se buscan las cuentas.
     * @return Una lista de objetos {@link CuentaResponse} con la información de todas las cuentas asociadas.
     */
    @Operation(summary = "Obtener todas las cuentas de un cliente por GUID",
            description = "Recupera todas las cuentas asociadas a un cliente específico utilizando su GUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })

    @Override
    public ArrayList<CuentaResponse> getAllCuentasByClienteGuid(String clienteGuid) {
        ArrayList<Cuenta> cuentas = cuentaRepository.findAllByCliente_Guid(clienteGuid);

        ArrayList<CuentaResponse> cuentaResponses = new ArrayList<>();
        cuentas.forEach(cuenta -> {
            CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(
                    cuenta,
                    cuenta.getTipoCuenta().getGuid(),
                    cuenta.getTarjeta().getGuid(),
                    cuenta.getCliente().getGuid()
            );
            cuentaResponses.add(cuentaResponse);
        });

        return cuentaResponses;
    }

    /**
     * Recupera una cuenta por su identificador (GUID).
     * Esta operación consulta la base de datos para obtener los detalles de una cuenta específica
     * utilizando su identificador único (GUID).
     *
     * @param id El identificador único de la cuenta (GUID).
     * @return El objeto {@link CuentaResponse} que representa la información de la cuenta.
     * @throws CuentaNotFound Si la cuenta no existe en la base de datos.
     */
    @Operation(summary = "Obtener cuenta por GUID",
            description = "Obtiene la información de una cuenta utilizando su identificador GUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @Override
    @Cacheable
    public CuentaResponse getById(String id) {
        log.info("Obteniendo la cuenta con id: {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(() -> new CuentaNotFound(id));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    /**
     * Recupera una cuenta utilizando su IBAN.
     * Esta operación consulta la base de datos para localizar una cuenta por su número IBAN.
     *
     * @param iban El número IBAN de la cuenta.
     * @return El objeto {@link CuentaResponse} que representa la información de la cuenta.
     * @throws CuentaNotFoundByIban Si la cuenta con el número IBAN no existe en la base de datos.
     */
    @Operation(summary = "Obtener cuenta por IBAN",
            description = "Obtiene la información de una cuenta utilizando su IBAN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada por IBAN")
    })
    @Override
    public CuentaResponse getByIban(String iban) {
        var cuenta = cuentaRepository.findByIban(iban).orElseThrow(() -> new CuentaNotFoundByIban(iban));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    /**
     * Guarda una nueva cuenta en la base de datos.
     *
     * @param cuentaRequest Solicitud de información de cuenta para guardar.
     * @return La cuenta guardada con su información mapeada.
     */
    @Override
    public CuentaResponse getByNumTarjeta(String numTarjeta) {
        var tarjeta = tarjetaRepository.findByNumeroTarjeta(numTarjeta).orElseThrow(() -> new TarjetaNotFoundByNumero(numTarjeta));
        var cuenta = cuentaRepository.findByTarjetaId(tarjeta.getId()).orElseThrow(() -> new CuentaNotFoundByNumTarjeta(tarjeta.getId().toString()));
        return cuentaMapper.toCuentaResponse(cuenta, cuenta.getTipoCuenta().getGuid(), cuenta.getTarjeta().getGuid(), cuenta.getCliente().getGuid());
    }

    @Override
    @CachePut
    @Operation(summary = "Guardar una nueva cuenta", description = "Crea una cuenta en el sistema con la información proporcionada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public CuentaResponse save(CuentaRequest cuentaRequest) {
        log.info("Guardando cuenta: {}", cuentaRequest);
        var tipoCuenta = tipoCuentaRepository.findByGuid(cuentaRequest.getTipoCuentaId()).orElseThrow(
                () -> new TipoCuentaNotFound(cuentaRequest.getTipoCuentaId())
        );
        var tarjeta = tarjetaRepository.findByGuid(cuentaRequest.getTarjetaId()).orElseThrow(
                () -> new TarjetaNotFound(cuentaRequest.getTarjetaId())
        );
        var cliente = clienteRepository.findByGuid(cuentaRequest.getClienteId()).orElseThrow(
                () -> new ClienteNotFound(cuentaRequest.getClienteId())
        );
        var cuentaSaved = cuentaRepository.save(cuentaMapper.toCuenta(tipoCuenta, tarjeta, cliente));

        // Actualizamos el listado de cuentas del cliente
        cliente.getCuentas().add(cuentaSaved);
        clienteRepository.save(cliente);

        // Forzamos sincronización y evitamos cache en siguiente busqueda de cliente
        clienteRepository.flush();
        evictClienteCache(cliente.getGuid());

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaSaved, cuentaSaved.getTipoCuenta().getGuid(), cuentaSaved.getTarjeta().getGuid(), cuentaSaved.getCliente().getGuid());

        onChange(Notification.Tipo.CREATE, cuentaResponse, cuentaSaved);
        return cuentaMapper.toCuentaResponse(cuentaSaved, cuentaSaved.getTipoCuenta().getGuid(), cuentaSaved.getTarjeta().getGuid(), cuentaSaved.getCliente().getGuid());
    }
    /**
     * Actualiza la información de una cuenta específica en el sistema.
     * Permite actualizar la relación con el tipo de cuenta, la tarjeta y el cliente asociado si se especifican en la solicitud.
     *
     * @param id El identificador único de la cuenta que se desea actualizar.
     * @param cuentaRequestUpdate El objeto que contiene la información actualizada para la cuenta.
     * @return El objeto {@link CuentaResponse} con los datos actualizados de la cuenta.
     * @throws CuentaNotFound Si la cuenta no existe en el sistema.
     * @throws TipoCuentaNotFound Si el tipo de cuenta no existe en el sistema.
     * @throws TarjetaNotFound Si la tarjeta no existe en el sistema.
     * @throws ClienteNotFound Si el cliente no existe en el sistema.
     * @throws JsonProcessingException Si ocurre un error al procesar la información JSON al enviar notificaciones.
     *
     * @see CuentaRepository
     * @see TipoCuentaRepository
     * @see TarjetaRepository
     * @see ClienteRepository
     * @see CuentaResponse
     * @see CuentaRequestUpdate
     *
     * @author Jaime León, Natalia González,
     *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    @Override
    @CachePut
    @Operation(
            summary = "Actualizar información de la cuenta",
            description = "Actualiza la información de una cuenta específica, incluyendo su tipo de cuenta, tarjeta y cliente asociado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta, tipo de cuenta, tarjeta o cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto al intentar actualizar con datos inválidos"),
            @ApiResponse(responseCode = "400", description = "Datos de tipo cuenta, tarjeta o cliente inválidos")
    })
    public CuentaResponse update(String id, CuentaRequestUpdate cuentaRequestUpdate) {
        log.info("Actualizando cuenta con id {}", id);
        var cuenta = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );

        var tipoCuenta = cuenta.getTipoCuenta();
        var tarjeta = cuenta.getTarjeta();
        var cliente = cuenta.getCliente();

        if (!cuentaRequestUpdate.getTipoCuentaId().isEmpty()) {
            tipoCuenta = tipoCuentaRepository.findByGuid(cuentaRequestUpdate.getTipoCuentaId()).orElseThrow(
                    () -> new TipoCuentaNotFound(cuentaRequestUpdate.getTipoCuentaId())
            );
        }

        if (!cuentaRequestUpdate.getTarjetaId().isEmpty()) {
            tarjeta = tarjetaRepository.findByGuid(cuentaRequestUpdate.getTarjetaId()).orElseThrow(
                    () -> new TarjetaNotFound(cuentaRequestUpdate.getTarjetaId())
            );
        }

        if (!cuentaRequestUpdate.getClienteId().isEmpty()) {
            cliente = clienteRepository.findByGuid(cuentaRequestUpdate.getClienteId()).orElseThrow(
                    () -> new ClienteNotFound(cuentaRequestUpdate.getClienteId())
            );
        }

        var cuentaUpdated = cuentaRepository.save(cuentaMapper.toCuentaUpdate(cuentaRequestUpdate, cuenta, tipoCuenta, tarjeta, cliente));

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaUpdated, cuentaUpdated.getTipoCuenta().getGuid(), cuentaUpdated.getTarjeta().getGuid(), cuentaUpdated.getCliente().getGuid());

        onChange(Notification.Tipo.UPDATE, cuentaResponse, cuentaUpdated);
        return cuentaMapper.toCuentaResponse(cuentaUpdated, cuentaUpdated.getTipoCuenta().getGuid(), cuentaUpdated.getTarjeta().getGuid(), cuentaUpdated.getCliente().getGuid());
    }

    /**
     * Elimina una cuenta de forma lógica estableciendo el campo 'isDeleted' como verdadero.
     * Envía una notificación mediante WebSocket a los clientes para informar del cambio.
     *
     * @param id El identificador único de la cuenta que se desea eliminar.
     * @throws CuentaNotFound Si la cuenta no existe en el sistema.
     * @see CuentaRepository
     * @see CuentaResponse
     * @see Notification
     *
     * @author Jaime León, Natalia González,
     *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    @Override
    @CacheEvict
    @Operation(summary = "Eliminar Cuenta Lógicamente",
            description = "Elimina una cuenta de manera lógica estableciendo su campo 'isDeleted' como verdadero. " +
                    "Además, envía una notificación a los clientes conectados por WebSocket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public void deleteById(String id) {
        log.info("Eliminando cuenta con id {}", id);
        var cuentaExistente = cuentaRepository.findByGuid(id).orElseThrow(
                () -> new CuentaNotFound(id)
        );
        cuentaExistente.setIsDeleted(true);
        cuentaRepository.save(cuentaExistente);

        CuentaResponse cuentaResponse = cuentaMapper.toCuentaResponse(cuentaExistente, cuentaExistente.getTipoCuenta().getGuid(), cuentaExistente.getTarjeta().getGuid(), cuentaExistente.getCliente().getGuid());

        onChange(Notification.Tipo.DELETE, cuentaResponse, cuentaExistente);
    }

    /**
     * Elimina la información en caché relacionada con el cliente para asegurar que la información actualizada sea recuperada en la siguiente consulta.
     *
     * @param clienteGuid El identificador único del cliente cuya información en caché se desea invalidar.
     * @see CacheEvict
     * @see ClienteRepository
     * @see Notification
     *
     * @author Jaime León, Natalia González,
     *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    @CacheEvict
    @Operation(summary = "Invalidar caché del cliente",
            description = "Elimina la caché de un cliente específico para asegurar que la información actualizada sea cargada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caché del cliente invalidada correctamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public void evictClienteCache(String clienteGuid) {
        log.info("Invalidando la caché del cliente con GUID: {}", clienteGuid);
    }

    /**
     * Notifica los cambios en la base de datos mediante WebSocket a los clientes interesados.
     * Se utiliza para enviar eventos en tiempo real a través de una conexión WebSocket.
     *
     * @param tipo   Tipo de notificación (por ejemplo, creación, actualización, eliminación de cuenta).
     * @param data   Información detallada sobre la cuenta que ha sido modificada.
     * @param cuenta Cuenta actual que desencadena la notificación.
     * @throws UserNotFoundById Si el usuario correspondiente no se encuentra en la base de datos.
     * @see CuentaResponse
     * @see UserRepository
     * @see Notification
     *
     * @author Jaime León, Natalia González,
     *         German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
     * @version 1.0-SNAPSHOT
     */
    @Operation(summary = "Notificar cambios en la base de datos mediante WebSocket",
            description = "Envía una notificación a través de WebSocket con los detalles de una operación (crear/actualizar/eliminar cuenta).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación enviada con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error en el servidor al procesar la notificación")
    })
    void onChange(Notification.Tipo tipo, CuentaResponse data, Cuenta cuenta) {
        log.info("Servicio de Cuentas onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketCuentasHandler();
        }

        try {
            Notification<CuentaResponse> notificacion = new Notification<>(
                    "CUENTAS",
                    tipo,
                    cuentaMapper.toCuentaResponse(
                            cuenta,
                            data.getTipoCuentaId(),
                            data.getTarjetaId(),
                            data.getClienteId()
                            ),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el usuario del cliente de la cuenta
            String userId = cuenta.getCliente().getUser().getGuid();
            User user = userRepository.findByGuid(userId).orElseThrow(() -> new UserNotFoundById(userId));
            String userName = user.getUsername();

            log.info("Enviando mensaje al cliente ws del usuario");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessageToUser(userName,json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
}