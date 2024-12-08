package org.example.vivesbankproject.rest.movimientos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import org.example.vivesbankproject.rest.cliente.exceptions.ClienteNotFoundByUser;
import org.example.vivesbankproject.rest.cliente.service.ClienteService;
import org.example.vivesbankproject.rest.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFound;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByClienteGuid;
import org.example.vivesbankproject.rest.cuenta.exceptions.cuenta.CuentaNotFoundByTarjetaId;
import org.example.vivesbankproject.rest.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.rest.cuenta.services.CuentaService;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoRequest;
import org.example.vivesbankproject.rest.movimientos.dto.MovimientoResponse;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.DuplicatedDomiciliacionException;
import org.example.vivesbankproject.rest.movimientos.exceptions.domiciliacion.SaldoInsuficienteException;
import org.example.vivesbankproject.rest.movimientos.exceptions.movimientos.*;
import org.example.vivesbankproject.rest.movimientos.mappers.MovimientoMapper;
import org.example.vivesbankproject.rest.movimientos.models.*;
import org.example.vivesbankproject.rest.movimientos.repositories.DomiciliacionRepository;
import org.example.vivesbankproject.rest.movimientos.repositories.MovimientosRepository;
import org.example.vivesbankproject.rest.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.rest.tarjeta.exceptions.TarjetaNotFoundByNumero;
import org.example.vivesbankproject.rest.tarjeta.service.TarjetaService;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.services.UserService;
import org.example.vivesbankproject.config.websockets.WebSocketConfig;
import org.example.vivesbankproject.config.websockets.WebSocketHandler;
import org.example.vivesbankproject.websocket.notifications.dto.DomiciliacionResponse;
import org.example.vivesbankproject.websocket.notifications.dto.IngresoNominaResponse;
import org.example.vivesbankproject.websocket.notifications.dto.PagoConTarjetaResponse;
import org.example.vivesbankproject.websocket.notifications.dto.TransferenciaResponse;
import org.example.vivesbankproject.websocket.notifications.mappers.NotificationMapper;
import org.example.vivesbankproject.websocket.notifications.models.Notification;
import org.example.vivesbankproject.utils.validators.ValidarCif;
import org.example.vivesbankproject.utils.validators.ValidarIban;
import org.example.vivesbankproject.utils.validators.ValidarTarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Implementación del servicio para gestionar operaciones relacionadas con movimientos.
 * Implementa la interfaz MovimientosService con funcionalidades para obtener movimientos
 * por su ID, por GUID y con soporte de almacenamiento en caché.
 *  @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 *  @version 1.0-SNAPSHOT
 */
@Slf4j
@Service
@CacheConfig(cacheNames = {"Movimientos"})
public class MovimientosServiceImpl implements MovimientosService {

    private final ClienteService clienteService;
    private final MovimientosRepository movimientosRepository;
    private final DomiciliacionRepository domiciliacionRepository;
    private final CuentaService cuentaService;
    private final MovimientoMapper movimientosMapper;
    private final TarjetaService tarjetaService;
    private final CuentaMapper cuentaMapper;
    private final UserService userService;
    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final NotificationMapper notificationMapper;
    // Para los test
    @Setter
    private WebSocketHandler webSocketService;
    /**
     * Constructor para inyectar las dependencias necesarias en el servicio.
     *
     * @param cuentaService              Servicio para operaciones con cuentas
     * @param movimientosRepository      Repositorio para persistencia de datos de movimientos
     * @param clienteService            Servicio relacionado con operaciones de clientes
     * @param movimientosMapper         Mapeador para convertir modelos de datos de movimientos
     * @param domiciliacionRepository    Repositorio para operaciones relacionadas con domiciliaciones
     * @param tarjetaService            Servicio para trabajar con movimientos de tarjetas
     * @param userService               Servicio para operaciones relacionadas con usuarios
     * @param webSocketConfig           Configuración relacionada con conexiones WebSocket
     * @param notificationMapper        Mapeador para manejar operaciones de notificaciones
     * @param cuentaMapper              Mapeador para traducir modelos de datos de cuentas
     */


    @Autowired
    public MovimientosServiceImpl( CuentaService cuentaService, MovimientosRepository movimientosRepository, ClienteService clienteService, MovimientoMapper movimientosMapper, DomiciliacionRepository domiciliacionRepository, TarjetaService tarjetaService, UserService userService, WebSocketConfig webSocketConfig, NotificationMapper notificationMapper, CuentaMapper cuentaMapper) {
        this.clienteService = clienteService;
        this.movimientosRepository = movimientosRepository;
        this.movimientosMapper = movimientosMapper;
        this.domiciliacionRepository = domiciliacionRepository;
        this.cuentaService = cuentaService;
        this.tarjetaService = tarjetaService;
        this.cuentaMapper = cuentaMapper;


        this.userService = userService;
        this.webSocketConfig = webSocketConfig;

        webSocketService = webSocketConfig.webSocketMovimientosHandler();
        mapper = new ObjectMapper();
        this.notificationMapper = notificationMapper;

        }
    /**
     * Recupera todos los movimientos con soporte de paginación.
     *
     * @param pageable Objeto que contiene la información para la paginación
     * @return Una lista paginada de objetos {@link MovimientoResponse}
     */
    @Override
    @Operation(summary = "Obtener todos los movimientos", description = "Recupera una lista paginada de todos los movimientos")
    public Page<MovimientoResponse> getAll(Pageable pageable) {
        log.info("Encontrando todos los Movimientos");
        return movimientosRepository.findAll(pageable).map(movimientosMapper::toMovimientoResponse);
    }

    /**
     * Recupera un movimiento por su ID de forma segura utilizando almacenamiento en caché.
     *
     * @param _id El identificador único del movimiento
     * @return El objeto {@link MovimientoResponse} correspondiente al movimiento
     * @throws MovimientoNotFound Si el movimiento no se encuentra en la base de datos
     */
    @Override
    @Cacheable
    @Operation(summary = "Obtener movimiento por ID", description = "Recupera un movimiento específico por su identificador único")
    @ApiResponse(responseCode = "200", description = "Operación exitosa",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public MovimientoResponse getById(ObjectId _id) {
        log.info("Encontrando Movimiento por id: {}", _id);
        return movimientosRepository.findById(_id)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(_id));
    }
    /**
     * Recupera un movimiento por su GUID de forma segura utilizando almacenamiento en caché.
     *
     * @param guidMovimiento El identificador único de tipo GUID para identificar el movimiento
     * @return El objeto {@link MovimientoResponse} correspondiente al movimiento
     * @throws MovimientoNotFound Si el movimiento no se encuentra en la base de datos
     */
    @Override
    @Cacheable
    @Operation(summary = "Obtener movimiento por GUID", description = "Recupera un movimiento específico por su GUID")
    @ApiResponse(responseCode = "200", description = "Operación exitosa",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public MovimientoResponse getByGuid(String guidMovimiento) {
        log.info("Encontrando Movimiento por guid: {}", guidMovimiento);
        return movimientosRepository.findByGuid(guidMovimiento)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new MovimientoNotFound(guidMovimiento));
    }
    /**
     * Recupera los movimientos de un cliente específico utilizando su identificador GUID.
     *
     * @param ClienteGuid El identificador único del cliente (GUID).
     * @return El primer movimiento encontrado relacionado con el cliente.
     * @throws ClienteHasNoMovements Si el cliente no tiene movimientos o no existe.
     */
    @Override
    @Cacheable
    @Operation(summary = "Obtener movimientos por el identificador del cliente",
            description = "Recupera movimientos para un cliente específico utilizando su identificador GUID.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cliente no tiene movimientos o no existe")
    public MovimientoResponse getByClienteGuid(String ClienteGuid) {
        log.info("Encontrando Movimientos por idCliente: {}", ClienteGuid);
        clienteService.getById(ClienteGuid);
        return movimientosRepository.findByClienteGuid(ClienteGuid)
                .map(movimientosMapper::toMovimientoResponse)
                .orElseThrow(() -> new ClienteHasNoMovements(ClienteGuid));
    }
    /**
     * Guarda un nuevo movimiento en el repositorio después de validar la existencia del cliente.
     *
     * @param movimientoRequest El objeto de solicitud con los datos necesarios para crear el movimiento.
     * @return El objeto {@link MovimientoResponse} del movimiento guardado.
     */
    @Override
    @CachePut
    @Operation(summary = "Guardar un movimiento",
            description = "Crea y guarda un nuevo movimiento en el repositorio, actualizando la caché.")
    @ApiResponse(responseCode = "200", description = "Movimiento guardado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    public MovimientoResponse save(MovimientoRequest movimientoRequest) {
        log.info("Guardando Movimiento: {}", movimientoRequest);
        clienteService.getById(movimientoRequest.getClienteGuid());
        Movimiento movimiento = movimientosMapper.toMovimiento(movimientoRequest);
        var savedMovimiento = movimientosRepository.save(movimiento);
        return movimientosMapper.toMovimientoResponse(savedMovimiento);
    }
    /**
     * Guarda una nueva domiciliación para un cliente después de realizar las siguientes validaciones:
     * - Validar el formato de los IBAN de origen y destino.
     * - Validar que el cliente exista.
     * - Validar que la cuenta asociada exista.
     * - Validar que el monto sea mayor que cero.
     * - Validar duplicidad en la información de las domiciliaciones.
     *
     * @param user El usuario autenticado que intenta realizar la operación.
     * @param domiciliacion Objeto con la información de la domiciliación a guardar.
     * @return El objeto {@link Domiciliacion} que se ha guardado exitosamente en el repositorio.
     * @throws ClienteNotFoundByUser Si el cliente no se encuentra relacionado con el usuario.
     * @throws CuentaNotFound Si la cuenta especificada no existe.
     * @throws UnknownIban Si la cuenta no coincide con el identificador proporcionado.
     * @throws DuplicatedDomiciliacionException Si la domiciliación ya existe para el cliente.
     * @throws NegativeAmount Si la cantidad de la domiciliación es menor o igual a cero.
     */
    @Override
    @Operation(summary = "Guardar una domiciliación",
            description = "Guarda una domiciliación para un cliente después de validar la información del IBAN y restricciones relacionadas.")
    @ApiResponse(responseCode = "200", description = "Domiciliación guardada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Domiciliacion.class)))
    @ApiResponse(responseCode = "400", description = "El IBAN es inválido, cliente no existe, o la cantidad no es válida")
    public Domiciliacion saveDomiciliacion(User user, Domiciliacion domiciliacion) {
        log.info("Guardando Domiciliacion: {}", domiciliacion);
        // validar Iban correcto
        ValidarIban.validateIban(domiciliacion.getIbanOrigen());
        ValidarIban.validateIban(domiciliacion.getIbanDestino());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(domiciliacion.getIbanOrigen());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(domiciliacion.getIbanOrigen());
        }

        if (!cliente.getGuid().equals(clienteCuenta.getClienteId())) {
            throw new UnknownIban(domiciliacion.getIbanOrigen());
        }

        // Validar si la domiciliación ya existe
        var clienteDomiciliaciones = domiciliacionRepository.findByClienteGuid(cliente.getGuid());
        if (clienteDomiciliaciones.stream().anyMatch(d -> d.getIbanDestino().equals(domiciliacion.getIbanDestino()))) {
            throw new DuplicatedDomiciliacionException(domiciliacion.getIbanDestino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadDomiciliacion = new BigDecimal(domiciliacion.getCantidad().toString());
        if (cantidadDomiciliacion.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadDomiciliacion);
        }

        // Guardar la domiciliación
        domiciliacion.setUltimaEjecucion(LocalDateTime.now()); // Registro inicial
        domiciliacion.setClienteGuid(cliente.getGuid()); // Asigno el id del cliente al domiciliación

        // Notificación al cliente
        onChangeDomiciliacion(Notification.Tipo.CREATE,domiciliacion);

        // Retornar respuesta
        return domiciliacionRepository.save(domiciliacion);
    }
    /**
     * Guarda un nuevo ingreso de nómina después de validar lo siguiente:
     * - Validar el formato del IBAN de origen y destino.
     * - Validar el CIF de la empresa.
     * - Validar que el cliente existe en el sistema.
     * - Validar que la cuenta existe.
     * - Validar que el monto de nómina es mayor que cero.
     *
     * @param user El usuario autenticado que intenta realizar la operación.
     * @param ingresoDeNomina Objeto con los datos del ingreso de nómina.
     * @return El objeto {@link MovimientoResponse} correspondiente al movimiento guardado.
     * @throws ClienteNotFoundByUser Si el cliente no existe en el sistema.
     * @throws CuentaNotFound Si la cuenta de destino no existe.
     * @throws NegativeAmount Si el monto de nómina es negativo o cero.
     */
    @Override
    @Operation(summary = "Guardar ingreso de nómina",
            description = "Crea y guarda un nuevo ingreso de nómina después de realizar las validaciones necesarias.")
    @ApiResponse(responseCode = "200", description = "Ingreso de nómina guardado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    public MovimientoResponse saveIngresoDeNomina(User user, IngresoDeNomina ingresoDeNomina) {
        log.info("Guardando Ingreso de Nomina: {}", ingresoDeNomina);
        // Validar Iban correcto
        ValidarIban.validateIban(ingresoDeNomina.getIban_Destino());
        ValidarIban.validateIban(ingresoDeNomina.getIban_Origen());
        // Validar Cif
        ValidarCif.validateCif(ingresoDeNomina.getCifEmpresa());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var clienteCuenta = cuentaService.getByIban(ingresoDeNomina.getIban_Destino());
        if (clienteCuenta == null) {
            throw new CuentaNotFound(ingresoDeNomina.getIban_Destino());
        }

        // Validar que el ingreso de nomina es > 0
        var cantidadNomina = new BigDecimal(ingresoDeNomina.getCantidad().toString());
        if (cantidadNomina.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadNomina);
        }

        // sumar al cliente
        var saldoActual = new BigDecimal(clienteCuenta.getSaldo());
        clienteCuenta.setSaldo(String.valueOf(saldoActual.add(cantidadNomina)));
        cuentaService.update(clienteCuenta.getGuid(), cuentaMapper.toCuentaRequestUpdate(clienteCuenta));

        // Crear el movimiento
        Movimiento movimineto = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .ingresoDeNomina(ingresoDeNomina)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimineto);
        onChangeIngresoNomina(Notification.Tipo.CREATE,ingresoDeNomina);
        return movimientosMapper.toMovimientoResponse(saved);
    }
    /**
     * Guarda un pago realizado con tarjeta después de realizar las siguientes validaciones:
     * - Validar el número de la tarjeta.
     * - Validar que el cliente exista en el sistema.
     * - Validar que la tarjeta existe.
     * - Validar que la cuenta asociada a la tarjeta exista.
     * - Validar que el monto sea positivo y verificar que el cliente tenga saldo suficiente.
     *
     * @param user El usuario autenticado que intenta realizar la operación.
     * @param pagoConTarjeta Objeto con la información del pago realizado con tarjeta.
     * @return El objeto {@link MovimientoResponse} correspondiente al movimiento guardado.
     * @throws ClienteNotFoundByUser Si el cliente no existe en el sistema.
     * @throws TarjetaNotFoundByNumero Si la tarjeta no existe.
     * @throws CuentaNotFoundByClienteGuid Si el cliente no tiene cuentas asociadas.
     * @throws CuentaNotFoundByTarjetaId Si la tarjeta no está asociada correctamente a una cuenta.
     * @throws NegativeAmount Si el monto del pago es negativo.
     * @throws SaldoInsuficienteException Si el cliente no tiene saldo suficiente.
     */
    @Override
    @Operation(summary = "Guardar pago con tarjeta",
            description = "Procesa y guarda un pago con tarjeta después de realizar las validaciones necesarias.")
    @ApiResponse(responseCode = "200", description = "Pago con tarjeta guardado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    public MovimientoResponse savePagoConTarjeta(User user, PagoConTarjeta pagoConTarjeta) {
        log.info("Guardando Pago con Tarjeta: {}", pagoConTarjeta);
        // Validar Iban correcto
        ValidarTarjeta.validateTarjeta(pagoConTarjeta.getNumeroTarjeta());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la tarjeta existe
        var clienteTarjeta = tarjetaService.getByNumeroTarjeta(pagoConTarjeta.getNumeroTarjeta());
        if (clienteTarjeta == null) {
            throw new TarjetaNotFoundByNumero(pagoConTarjeta.getNumeroTarjeta());
        }


        // Validar que la cuenta existe
        var clienteCuentas = cuentaService.getAllCuentasByClienteGuid(cliente.getGuid());
        if (clienteCuentas == null) {
            throw new CuentaNotFoundByClienteGuid(cliente.getGuid());
        }

        // Validar que la cuenta asociada a la tarjeta existe
        var cuentaAsociadaATarjeta = clienteCuentas.stream()
                .filter(c -> c.getTarjetaId().equals(clienteTarjeta.getGuid()))
                .findFirst()
                .orElseThrow(() -> new CuentaNotFoundByTarjetaId(clienteTarjeta.getGuid()));


        // Validar que la cantidad es mayor que cero
        var cantidadTarjeta = new BigDecimal(pagoConTarjeta.getCantidad().toString());
        if (cantidadTarjeta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTarjeta);
        }

        // Validar saldo suficiente
        BigDecimal saldoActual = new BigDecimal(cuentaAsociadaATarjeta.getSaldo());
        if (saldoActual.compareTo(cantidadTarjeta) < 0) {
            throw new SaldoInsuficienteException(cuentaAsociadaATarjeta.getIban(), saldoActual);
        }

        // restar al cliente
        saldoActual = saldoActual.subtract(cantidadTarjeta);
        cuentaAsociadaATarjeta.setSaldo(String.valueOf(saldoActual));
        cuentaService.update(cuentaAsociadaATarjeta.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaAsociadaATarjeta));

        //crear el movimiento
        Movimiento movimiento = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .pagoConTarjeta(pagoConTarjeta)
                .build();

        // Guardar el movimiento
        Movimiento saved = movimientosRepository.save(movimiento);
        onChangePagoConTarjeta(Notification.Tipo.CREATE,pagoConTarjeta);

        return movimientosMapper.toMovimientoResponse(saved);

    }
    /**
     * Guarda una transferencia entre dos cuentas después de realizar las siguientes validaciones:
     * - Validar que los IBAN de origen y destino sean correctos.
     * - Validar que el cliente existe.
     * - Validar que las cuentas involucradas existan y sean válidas.
     * - Validar que el monto sea positivo y que haya saldo suficiente en la cuenta de origen.
     *
     * @param user El usuario autenticado que intenta realizar la operación.
     * @param transferencia Objeto con la información de la transferencia.
     * @return El objeto {@link MovimientoResponse} correspondiente al movimiento guardado.
     * @throws ClienteNotFoundByUser Si el cliente no existe en el sistema.
     * @throws CuentaNotFound Si alguna de las cuentas no existe.
     * @throws SaldoInsuficienteException Si no hay saldo suficiente en la cuenta origen.
     * @throws NegativeAmount Si el monto es negativo.
     */
    @Override
    @Operation(summary = "Guardar transferencia",
            description = "Procesa y guarda una transferencia entre dos cuentas después de validar las restricciones necesarias.")
    @ApiResponse(responseCode = "200", description = "Transferencia guardada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class)))
    public MovimientoResponse saveTransferencia(User user, Transferencia transferencia) {
        log.info("Guardando Transferencia: {}", transferencia);

        // Validar Iban correcto
        ValidarIban.validateIban(transferencia.getIban_Destino());
        ValidarIban.validateIban(transferencia.getIban_Origen());

        // Validar que el cliente existe
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (cliente == null) {
            throw new ClienteNotFoundByUser(user.getGuid());
        }

        // Validar que la cuenta existe
        var cuentaOrigen = cuentaService.getByIban(transferencia.getIban_Origen());
        if (cuentaOrigen == null) {
            throw new CuentaNotFound(transferencia.getIban_Origen());
        }

        if (!cliente.getGuid().equals(cuentaOrigen.getClienteId())) {
            throw new UnknownIban(transferencia.getIban_Origen());
        }

        // HACER MOVIMIENTO INVERSO
        var cuentaDestino = cuentaService.getByIban(transferencia.getIban_Destino());
        if (cuentaDestino == null) {
            throw new CuentaNotFound(transferencia.getIban_Destino());
        }

        // Validar que la cantidad es mayor que cero
        var cantidadTranseferencia = new BigDecimal(transferencia.getCantidad().toString());
        if (cantidadTranseferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmount(cantidadTranseferencia);
        }

        // Validar saldo suficiente
        BigDecimal saldoActual = new BigDecimal(cuentaOrigen.getSaldo());
        if (saldoActual.compareTo(cantidadTranseferencia) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigen.getIban(), saldoActual);
        }

        // restar al cliente
        saldoActual = saldoActual.subtract(cantidadTranseferencia);
        cuentaOrigen.setSaldo(String.valueOf(saldoActual));
        cuentaService.update(cuentaOrigen.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaOrigen));

        // sumar al cliente de la cuenta destino
        var saldoActualDestino = new BigDecimal(cuentaDestino.getSaldo());
        saldoActualDestino = saldoActualDestino.add(cantidadTranseferencia);
        cuentaDestino.setSaldo(String.valueOf(saldoActualDestino));
        cuentaService.update(cuentaDestino.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaDestino));

        // crear el movimiento al cliente destino
        log.info("Crear movimiento destino");
        Movimiento movimientoDestino = Movimiento.builder()
                .clienteGuid(cuentaDestino.getClienteId())
                .transferencia(transferencia)
                .build();

        // Guardar el movimiento destino
        log.info("Guardar movimiento destino");
        movimientosRepository.save(movimientoDestino);

        // crear el movimiento al cliente origen
        Movimiento movimientoOrigen = Movimiento.builder()
                .clienteGuid(cliente.getGuid())
                .transferencia(Transferencia.builder()
                        .iban_Origen(transferencia.getIban_Origen())
                        .iban_Destino(transferencia.getIban_Destino())
                        .cantidad(transferencia.getCantidad().negate())
                        .nombreBeneficiario(transferencia.getNombreBeneficiario())
                        .movimientoDestino(movimientoDestino.getGuid())
                        .build())
                .build();

        // Guardar el movimiento origen
        var saved = movimientosRepository.save(movimientoOrigen);
        onChangeTransferencia(Notification.Tipo.CREATE,transferencia);

        return movimientosMapper.toMovimientoResponse(saved);
    }
    /**
     * Revoca una transferencia específica identificada por su GUID.
     * Esta operación realiza una serie de validaciones, como verificar la validez de la solicitud,
     * la propiedad de la cuenta de origen, y si la transferencia se encuentra dentro del plazo de revocación permitido.
     * Además, revierte la transferencia ajustando el saldo de las cuentas involucradas y marca los movimientos
     * relacionados como eliminados.
     *
     * @param user                   El usuario que solicita la revocación de la transferencia
     * @param movimientoTransferenciaGuid El identificador único de la transferencia a revocar
     * @return El resultado de la operación de revocación como {@link MovimientoResponse}
     * @throws MovimientoNotFound si el movimiento no existe
     * @throws TransferenciaNoRevocableException si la transferencia no puede ser revocada
     * @throws MovimientoIsNotTransferenciaException si el movimiento no es una transferencia
     * @throws UnknownIban si el usuario no es el propietario de la cuenta de origen
     */
    @Override
    @Operation(
            summary = "Revocar una transferencia",
            description = "Revoca una transferencia dentro del plazo permitido y ajusta los saldos de las cuentas involucradas"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferencia revocada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos o solicitud no válida"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "409", description = "La transferencia no puede ser revocada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public MovimientoResponse revocarTransferencia(User user, String movimientoTransferenciaGuid) {
        log.info("Revocando Transferencia: {}", movimientoTransferenciaGuid);

        // Obtener el movimiento original
        var movimientoOriginal = movimientosRepository.findByGuid(movimientoTransferenciaGuid)
                .orElseThrow(() -> new MovimientoNotFound(movimientoTransferenciaGuid));

        // validar que no haya pasado 1 dia
        if (!LocalDateTime.now().isBefore(movimientoOriginal.getCreatedAt().plusDays(1))) {
            throw new TransferenciaNoRevocableException(movimientoTransferenciaGuid);
        }

        // Verificar que el movimiento es una transferencia
        if (movimientoOriginal.getTransferencia() == null) {
            throw new MovimientoIsNotTransferenciaException(movimientoTransferenciaGuid);
        }

        // Verificar que el usuario que solicita la revocación es el propietario de la cuenta de origen
        var cliente = clienteService.getUserAuthenticatedByGuid(user.getGuid());
        if (!cliente.getGuid().equals(movimientoOriginal.getClienteGuid())) {
            throw new UnknownIban(movimientoOriginal.getTransferencia().getIban_Origen());
        }

        // Obtener las cuentas involucradas
        var cuentaOrigen = cuentaService.getByIban(movimientoOriginal.getTransferencia().getIban_Origen());
        var cuentaDestino = cuentaService.getByIban(movimientoOriginal.getTransferencia().getIban_Destino());

        // Revertir la transferencia

        // Restar de la cuenta destino
        BigDecimal cantidadTransferencia = movimientoOriginal.getTransferencia().getCantidad();

        BigDecimal saldoDestino = new BigDecimal(cuentaDestino.getSaldo());
        saldoDestino = saldoDestino.add(cantidadTransferencia);
        cuentaDestino.setSaldo(saldoDestino.toString());
        cuentaService.update(cuentaDestino.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaDestino));

        // Sumar a la cuenta origen
        BigDecimal saldoOrigen = new BigDecimal(cuentaOrigen.getSaldo());
        saldoOrigen = saldoOrigen.subtract(cantidadTransferencia);
        cuentaOrigen.setSaldo(saldoOrigen.toString());
        cuentaService.update(cuentaOrigen.getGuid(), cuentaMapper.toCuentaRequestUpdate(cuentaOrigen));

        // Marcar el movimiento original como revocado (si es necesario)
        var movimientoOriginalDestino = movimientosRepository.findByGuid(movimientoOriginal.getTransferencia().getMovimientoDestino()).orElseThrow(
                () -> new MovimientoNotFound(movimientoOriginal.getTransferencia().getMovimientoDestino())
        );

        // Marcar ambos movimientos como eliminados
        movimientoOriginal.setIsDeleted(true);
            onChangeTransferencia(Notification.Tipo.DELETE,movimientoOriginal.getTransferencia());
        movimientoOriginalDestino.setIsDeleted(true);
            onChangeTransferencia(Notification.Tipo.DELETE,movimientoOriginalDestino.getTransferencia());
        movimientosRepository.save(movimientoOriginalDestino);
            onChangeTransferencia(Notification.Tipo.CREATE,movimientoOriginalDestino.getTransferencia());
        movimientosRepository.save(movimientoOriginal);
            onChangeTransferencia(Notification.Tipo.CREATE,movimientoOriginal.getTransferencia());


        return movimientosMapper.toMovimientoResponse(movimientoOriginal);
    }
    /**
     * Envía una notificación por WebSocket cuando se realiza un cambio en los ingresos de nómina.
     * Convierte la información de `IngresoDeNomina` en un DTO y envía una notificación JSON a los clientes relevantes.
     *
     * @param tipo El tipo de operación de notificación que se enviará.
     * @param data Información relacionada con el ingreso de nómina que se notificará.
     */
    @Operation(
            summary = "Enviar notificación de ingreso de nómina",
            description = "Convierte la información de un cambio en los ingresos de nómina a JSON y envía una notificación a través de WebSocket"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar la notificación")
    })
    void onChangeIngresoNomina(Notification.Tipo tipo, IngresoDeNomina data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<IngresoNominaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toIngresoNominaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            String clienteId = cuentaService.getByIban(data.getIban_Destino()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
    /**
     * Envía una notificación por WebSocket cuando se realiza un cambio en una transferencia.
     * Convierte la información de la transferencia en un DTO y envía la notificación JSON tanto al ordenante como al beneficiario.
     *
     * @param tipo El tipo de operación de notificación que se enviará.
     * @param data Información relacionada con la transferencia que se notificará.
     */
    @Operation(
            summary = "Enviar notificación de transferencia",
            description = "Convierte la información de una transferencia en un DTO y envía una notificación a través de WebSocket tanto al ordenante como al beneficiario"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar la notificación")
    })
    void onChangeTransferencia(Notification.Tipo tipo, Transferencia data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<TransferenciaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toTransferenciaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Notificar tanto al ordenante como al beneficiario

            // Recuperar el cliente del usuario logueado (beneficiario)
            String clienteId = cuentaService.getByIban(data.getIban_Destino()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();
            sendMessageUser(userName, json);

            // Recuperar el cliente del usuario logueado (ordenante)
            clienteId = cuentaService.getByIban(data.getIban_Origen()).getClienteId();
            userId = clienteService.getById(clienteId).getUserId();
            userName = userService.getById(userId).getUsername();
            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    /**
     * Envía una notificación por WebSocket cuando se realiza un cambio en la domiciliación.
     * Convierte la información de `Domiciliacion` en un DTO y envía una notificación JSON al cliente correspondiente.
     *
     * @param tipo El tipo de operación de notificación que se enviará.
     * @param data Información relacionada con la domiciliación que se notificará.
     */
    @Operation(
            summary = "Enviar notificación de domiciliación",
            description = "Convierte la información de una domiciliación en un DTO y envía una notificación por WebSocket"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar la notificación")
    })

    void onChangeDomiciliacion(Notification.Tipo tipo, Domiciliacion data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<DomiciliacionResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toDomiciliacionDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            String clienteId = cuentaService.getByIban(data.getIbanOrigen()).getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }
    /**
     * Envía una notificación por WebSocket cuando se realiza un cambio en el pago con tarjeta.
     * Convierte la información de `PagoConTarjeta` en un DTO y envía la notificación JSON al cliente correspondiente.
     *
     * @param tipo El tipo de operación de notificación que se enviará.
     * @param data Información relacionada con el pago con tarjeta que se notificará.
     */
    @Operation(
            summary = "Enviar notificación de pago con tarjeta",
            description = "Convierte la información de un pago con tarjeta en un DTO y envía una notificación por WebSocket"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar la notificación")
    })
    void onChangePagoConTarjeta(Notification.Tipo tipo, PagoConTarjeta data) {
        log.info("Servicio de Movimientos onChange con tipo: {} y datos: {}", tipo, data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketMovimientosHandler();
        }

        try {
            Notification<PagoConTarjetaResponse> notificacion = new Notification<>(
                    "MOVIMIENTOS",
                    tipo,
                    notificationMapper.toPagoConTarjetaDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString(notificacion);

            // Recuperar el cliente del usuario logueado
            TarjetaResponse tarjeta = tarjetaService.getByNumeroTarjeta(data.getNumeroTarjeta());
            CuentaResponse cuenta =cuentaService.getByNumTarjeta(tarjeta.getNumeroTarjeta());
            String clienteId = cuenta.getClienteId();
            String userId = clienteService.getById(clienteId).getUserId();
            String userName = userService.getById(userId).getUsername();

            sendMessageUser(userName, json);

        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    /**
     * Envía un mensaje al usuario específico utilizando el servicio WebSocket.
     * El mensaje es enviado de forma asíncrona para evitar bloquear el hilo principal.
     *
     * @param userName El nombre del usuario al que se enviará el mensaje.
     * @param json Mensaje en formato JSON que se enviará.
     * @see WebSocketHandler
     */

    @Operation(
            summary = "Enviar mensaje a usuario específico",
            description = "Envía un mensaje JSON al usuario mediante WebSocket de forma asíncrona para evitar bloqueos en el hilo principal."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensaje enviado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar el mensaje a través del servicio WebSocket")
    })
    private void sendMessageUser(String userName, String json){
        log.info("Enviando mensaje al cliente ws del usuario");
        Thread senderThread = new Thread(() -> {
            try {
                webSocketService.sendMessageToUser(userName,json);
            } catch (Exception e) {
                log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
            }
        });
        senderThread.start();
    }
}
