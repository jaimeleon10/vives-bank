package org.example.vivesbankproject.rest.cliente.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.cliente.exceptions.*;
import org.example.vivesbankproject.rest.cliente.dto.ClienteProducto;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.rest.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.rest.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.rest.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.rest.cliente.models.Cliente;
import org.example.vivesbankproject.rest.cliente.models.Direccion;
import org.example.vivesbankproject.rest.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.rest.cuenta.dto.tipoCuenta.TipoCuentaResponseCatalogo;
import org.example.vivesbankproject.rest.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.rest.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.rest.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.rest.cuenta.repositories.TipoCuentaRepository;
import org.example.vivesbankproject.rest.storage.images.services.StorageImagesService;
import org.example.vivesbankproject.rest.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.rest.tarjeta.repositories.TarjetaRepository;
import org.example.vivesbankproject.rest.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.rest.users.models.User;
import org.example.vivesbankproject.rest.users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
/**
 * Implementación de nuestro servicio de clientes
 *
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Slf4j
@Service
@Tag(name = "Cliente", description = "Operaciones relacionadas con el servicio de clientes")
@CacheConfig(cacheNames={"cliente"})
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UserRepository userRepository;
    private final StorageImagesService storageImagesService;
    private final TarjetaRepository tarjetaRepository;
    private final CuentaRepository cuentaRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final TipoCuentaRepository tipoCuentaRepository;
    private final TipoCuentaMapper tipoCuentaMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, UserRepository userRepository, StorageImagesService storageImagesService, TarjetaRepository tarjetaRepository, CuentaRepository cuentaRepository, @Qualifier("stringRedisTemplate") RedisTemplate<String, String> redisTemplate, TipoCuentaRepository tipoCuentaRepository, TipoCuentaMapper tipoCuentaMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.userRepository = userRepository;
        this.storageImagesService = storageImagesService;
        this.cuentaRepository = cuentaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.redisTemplate = redisTemplate;
        this.tipoCuentaRepository = tipoCuentaRepository;
        this.tipoCuentaMapper = tipoCuentaMapper;
    }
    /**
     * Obtiene una lista de clientes aplicando filtros opcionales.
     *
     * @param dni Filtro por el número de identificación (DNI) del cliente.
     * @param nombre Filtro por el nombre del cliente.
     * @param apellidos Filtro por los apellidos del cliente.
     * @param email Filtro por el email del cliente.
     * @param telefono Filtro por el número de teléfono del cliente.
     * @param pageable Configuración de paginación para la consulta.
     * @return Devuelve una lista paginada de clientes que cumplen con los filtros.
     */

    @Override
    @Operation(summary = "Obtener todos los clientes con filtros", description = "Retorna una lista de clientes con los filtros aplicados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente")
    public Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable) {

        log.info("Obteniendo todos los clientes");
        Specification<Cliente> specDniCliente = (root, query, criteriaBuilder) ->
                dni.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("dni")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specNombreCliente = (root, query, criteriaBuilder) ->
                nombre.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specApellidosCliente = (root, query, criteriaBuilder) ->
                apellidos.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("apellidos")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specEmailCliente = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specTelefonoCliente = (root, query, criteriaBuilder) ->
                telefono.map(m -> criteriaBuilder.equal(root.get("telefono"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> criterio = Specification.where(specDniCliente)
                .and(specNombreCliente)
                .and(specApellidosCliente)
                .and(specEmailCliente)
                .and(specTelefonoCliente);

        Page<Cliente> clientePage = clienteRepository.findAll(criterio, pageable);

        return clientePage.map(cliente -> {
            String userId = cliente.getUser().getGuid();
            return clienteMapper.toClienteResponse(cliente, userId);
        });
    }

    /**
     * Obtiene un cliente específico por su identificador único.
     *
     * @param id Identificador único del cliente.
     * @return Devuelve la información del cliente encontrado.
     * @throws ClienteNotFound Si no se encuentra el cliente.
     */

    @Override
    @Operation(summary = "Obtener cliente por ID", description = "Retorna la información de un cliente por su identificador único")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado correctamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @Cacheable
    public ClienteResponse getById(  @Parameter(description = "Identificador único del cliente", required = true) String id) {
        log.info("Obteniendo cliente con guid: {}", id);
        var cliente = clienteRepository.findByGuid(id).orElseThrow(() -> new ClienteNotFound(id));
        String userId = cliente.getUser().getGuid();

        return clienteMapper.toClienteResponse(cliente, userId);
    }

    @Override
    public ClienteResponse getByDni(String dni) {
        log.info("Obteniendo cliente con dni: {}", dni);
        var cliente = clienteRepository.findByDni(dni).orElseThrow(() -> new ClienteNotFoundByDni(dni));
        String userId = cliente.getUser().getGuid();

        return clienteMapper.toClienteResponse(cliente, userId);
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     *
     * @param clienteRequestSave Información necesaria para crear un nuevo cliente.
     * @return Devuelve la información del cliente guardado.
     * @throws ClienteUserAlreadyAssigned Si el usuario ya está asignado a otro cliente.
     * @throws UserNotFoundById Si el usuario no existe en la base de datos.
     */

    @Override
    @Operation(summary = "Guardar un nuevo cliente", description = "Crea un nuevo cliente con la información proporcionada")
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Usuario ya asignado a otro cliente")
    @CachePut
    public ClienteResponse save( @Parameter(description = "Información para crear el cliente", required = true) ClienteRequestSave clienteRequestSave) {
        log.info("Guardando cliente");
        // Buscamos si existe algún cliente con el usuario adjunto ya asignado
        if (clienteRepository.existsByUserGuid(clienteRequestSave.getUserId())) {
            throw new ClienteUserAlreadyAssigned(clienteRequestSave.getUserId());
        }

        // Buscamos si existe el usuario por la id ajuntada en el cliente request
        var usuarioExistente = userRepository.findByGuid(clienteRequestSave.getUserId()).orElseThrow(
                () -> new UserNotFoundById(clienteRequestSave.getUserId())
        );

        var direccion = Direccion.builder()
                .calle(clienteRequestSave.getCalle())
                .numero(clienteRequestSave.getNumero())
                .codigoPostal(clienteRequestSave.getCodigoPostal())
                .piso(clienteRequestSave.getPiso())
                .letra(clienteRequestSave.getLetra())
                .build();

        // Mapeamos a cliente con el cliente request, el usuario existente y las cuentas existentes
        var cliente = clienteMapper.toCliente(clienteRequestSave, usuarioExistente, direccion);

        // Validamos datos (dni, email y teléfono) existentes
        validarClienteExistente(cliente);

        // Guardamos el cliente y lo mapeamos a response para devolverlo
        var clienteSaved = clienteRepository.save(cliente);
        return clienteMapper.toClienteResponse(clienteSaved, usuarioExistente.getGuid());
    }

    /**
     * Actualiza la información de un cliente en la base de datos.
     *
     * @param id Identificador único del cliente que se desea actualizar.
     * @param clienteRequestUpdate Información con los nuevos datos para actualizar el cliente.
     * @return ClienteResponse Devuelve la información actualizada del cliente.
     * @throws ClienteNotFound Si el cliente con el identificador proporcionado no existe.
     * @throws ClienteExistsByTelefono Si el número de teléfono ya existe en otro registro.
     * @throws ClienteExistsByEmail Si el correo electrónico ya existe en otro registro.
     */

    @Override
    @CachePut
    @Operation(summary = "Update Client Information",
            description = "Updates a client's information, including email, phone, and user association.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "404", description = "Client or user not found"),
            @ApiResponse(responseCode = "409", description = "User already assigned to another client"),
            @ApiResponse(responseCode = "400", description = "Email or phone already exists in the system")
    })
    public ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate) {
        log.info("Actualizando cliente con guid: {}", id);
        // Buscamos si existe el cliente con la el parámetro id
        var clienteExistente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        // Validamos si el nuevo email y telefono introducido existe en caso de que sea distinto del existente
        if (!Objects.equals(clienteRequestUpdate.getTelefono(), clienteExistente.getTelefono())) {
            if (clienteRepository.findByTelefono(clienteRequestUpdate.getTelefono()).isPresent()) {
                throw new ClienteExistsByTelefono(clienteRequestUpdate.getTelefono());
            }
        }
        if (!Objects.equals(clienteRequestUpdate.getEmail(), clienteExistente.getEmail())) {
            if (clienteRepository.findByEmail(clienteRequestUpdate.getEmail()).isPresent()) {
                throw new ClienteExistsByEmail(clienteRequestUpdate.getEmail());
            }
        }

        var direccion = Direccion.builder()
                .calle(clienteRequestUpdate.getCalle())
                .numero(clienteRequestUpdate.getNumero())
                .codigoPostal(clienteRequestUpdate.getCodigoPostal())
                .piso(clienteRequestUpdate.getPiso())
                .letra(clienteRequestUpdate.getLetra())
                .build();

        // Guardamos el cliente mapeado a update
        var clienteSave = clienteRepository.save(clienteMapper.toClienteUpdate(clienteRequestUpdate, clienteExistente, clienteExistente.getUser(), direccion));

        // Devolvemos el cliente response con los datos necesarios
        return clienteMapper.toClienteResponse(clienteSave, clienteExistente.getUser().getGuid());
    }

    /**
     * Elimina de forma lógica un cliente estableciendo el campo `isDeleted` como verdadero.
     *
     * @param id Identificador único del cliente que se desea borrar de forma lógica en la base de datos (soft delete).
     * @throws ClienteNotFound Si el cliente con el identificador proporcionado no existe en la base de datos.
     */

    @Override
    @CacheEvict
    @Transactional
    @Operation(summary = "Delete client by ID", description = "Marks a client as deleted by setting the 'isDeleted' flag to true.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully marked as deleted."),
            @ApiResponse(responseCode = "404", description = "Client with the provided ID not found."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error while attempting to delete the client.")
    })
    public void deleteById(String id) {
        log.info("Borrando cliente con guid: {}", id);
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );
        cliente.setIsDeleted(true);
        clienteRepository.save(cliente);
    }

    /**
     * Obtiene la información de un cliente autenticado utilizando su identificador único (GUID).
     *
     * @param guid Identificador único del usuario autenticado que se utilizará para buscar al cliente en la base de datos.
     * @return ClienteResponse Información del cliente autenticado.
     * @throws ClienteNotFoundByUser Si no se encuentra ningún cliente con el identificador proporcionado.
     * @throws UserNotFoundById Si no se encuentra el usuario asociado al cliente.
     */

    @Override
    @Cacheable
    @Operation(summary = "Get authenticated user by GUID", description = "Retrieves the authenticated user information by their unique user GUID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "404", description = "User associated with GUID is not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error during the search process")
    })
    public ClienteResponse getUserAuthenticatedByGuid(String guid) {
        log.info("Buscando cliente por user guid: {}", guid);

        // Buscamos el cliente directamente por el userGuid
        var cliente = clienteRepository.findByUserGuid(guid).orElseThrow(
                () -> new ClienteNotFoundByUser(guid)
        );

        // Obtenemos el usuario asociado al cliente
        var usuarioExistente = cliente.getUser();
        if (usuarioExistente == null) {
            throw new UserNotFoundById(guid);
        }

        return clienteMapper.toClienteResponse(cliente, usuarioExistente.getGuid());
    }

    /**
     * Actualiza la información de un cliente autenticado, permitiéndole cambiar su teléfono, email o dirección si es necesario.
     *
     * @param guid Identificador único del cliente autenticado para buscar su información.
     * @param clienteRequestUpdate Objeto que contiene los datos para la actualización, como teléfono, email y dirección.
     * @return ClienteResponse Información actualizada del cliente.
     * @throws ClienteNotFound Si el cliente autenticado no se encuentra en la base de datos.
     * @throws ClienteExistsByTelefono Si el teléfono ya está en uso por otro cliente.
     * @throws ClienteExistsByEmail Si el correo electrónico ya está en uso por otro cliente.
     */

    @Override
    @CachePut
    @Operation(summary = "Update authenticated user's information", description = "Allows an authenticated user to update their phone number, email, or address if applicable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Phone number or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error during the update process")
    })
    public ClienteResponse updateUserAuthenticated(  @Parameter(description = "Unique identifier of the authenticated user", required = true)
                                                         String guid,

                                                     @Parameter(description = "Updated user information", required = true)
                                                         ClienteRequestUpdate clienteRequestUpdate) {
        log.info("Actualizando cliente autenticado");
        var clienteAutenticado = clienteRepository.findByUserGuid(guid).orElseThrow(
                () -> new ClienteNotFound(guid)
        );

        if (!Objects.equals(clienteRequestUpdate.getTelefono(), clienteAutenticado.getTelefono())) {
            if (clienteRepository.findByTelefono(clienteRequestUpdate.getTelefono()).isPresent()) {
                throw new ClienteExistsByTelefono(clienteRequestUpdate.getTelefono());
            }
        }
        if (!Objects.equals(clienteRequestUpdate.getEmail(), clienteAutenticado.getEmail())) {
            if (clienteRepository.findByEmail(clienteRequestUpdate.getEmail()).isPresent()) {
                throw new ClienteExistsByEmail(clienteRequestUpdate.getEmail());
            }
        }

        var direccion = Direccion.builder()
                .calle(clienteRequestUpdate.getCalle())
                .numero(clienteRequestUpdate.getNumero())
                .codigoPostal(clienteRequestUpdate.getCodigoPostal())
                .piso(clienteRequestUpdate.getPiso())
                .letra(clienteRequestUpdate.getLetra())
                .build();

        // Guardamos el cliente mapeado a update
        var clienteSave = clienteRepository.save(clienteMapper.toClienteUpdate(clienteRequestUpdate, clienteAutenticado, clienteAutenticado.getUser(), direccion));

        // Devolvemos el cliente response con los datos necesarios
        return clienteMapper.toClienteResponse(clienteSave, clienteSave.getUser().getGuid());
    }

    /**
     * Ejecuta el derecho al olvido para eliminar un cliente.
     *
     * @param userGuid Identificador único del cliente que solicita el derecho al olvido.
     * @return Mensaje de confirmación tras borrar los datos personales.
     * @throws UserNotFoundById Si el usuario no existe en la base de datos.
     * @throws ClienteNotDeleted Si la eliminación de datos falla.
     */

    @Override
    @Operation(summary = "Ejecutar derecho al olvido", description = "Elimina la información personal del cliente en la base de datos")
    @ApiResponse(responseCode = "200", description = "Derecho al olvido ejecutado correctamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @CacheEvict(value = {"cliente", "usuario", "cuenta", "tarjeta"}, allEntries = true)
    public String derechoAlOlvido( @Parameter(description = "GUID del usuario para ejecutar derecho al olvido", required = true) String userGuid) {
        User usuario = userRepository.findByGuid(userGuid).orElseThrow(
                () -> new UserNotFoundById(userGuid)
        );
        Cliente cliente = clienteRepository.findByUserGuid(userGuid).orElseThrow(
                () -> new ClienteNotFoundByUser(userGuid)
        );

        try {
            clienteRepository.delete(cliente);
            userRepository.delete(usuario);
        } catch (Exception e) {
            throw new ClienteNotDeleted(cliente.getGuid());
        }

        clienteRepository.flush();
        userRepository.flush();
        tarjetaRepository.flush();
        cuentaRepository.flush();

        clearCacheByPrefix("clientes");
        clearCacheByPrefix("usuarios");
        clearCacheByPrefix("cuentas");
        clearCacheByPrefix("tarjetas");

        return "El cliente con guid '" + cliente.getGuid() + "' ejerció su derecho al olvido borrando todos sus datos personales";
    }

    /**
     * Valida que el cliente no exista ya en la base de datos por número de DNI, teléfono o email.
     * Lanza excepciones si alguno de estos datos ya existe en la base de datos.
     *
     * @param cliente Cliente que se va a validar.
     * @throws ClienteExistsByDni Si ya existe un cliente con el mismo número de DNI.
     * @throws ClienteExistsByTelefono Si ya existe un cliente con el mismo número de teléfono.
     * @throws ClienteExistsByEmail Si ya existe un cliente con el mismo correo electrónico.
     */

    @Operation(summary = "Validate Existing Client", description = "Validates if a client with the same DNI, phone number, or email already exists in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Client already exists with the same DNI"),
            @ApiResponse(responseCode = "400", description = "Client already exists with the same phone number"),
            @ApiResponse(responseCode = "400", description = "Client already exists with the same email")
    })
    public void validarClienteExistente(@Parameter(description = "Client object to validate", required = true) Cliente cliente) {
        if (clienteRepository.findByDni(cliente.getDni()).isPresent()) {
            throw new ClienteExistsByDni(cliente.getDni());
        }
        if (clienteRepository.findByTelefono(cliente.getTelefono()).isPresent()) {
            throw new ClienteExistsByTelefono(cliente.getTelefono());
        }
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new ClienteExistsByEmail(cliente.getEmail());
        }
    }

    /**
     * Actualiza la foto del DNI de un cliente. Se elimina la foto anterior si ya existe y se guarda la nueva foto.
     *
     * @param id Identificador único del cliente.
     * @param file Archivo de la nueva foto del DNI.
     * @return Devuelve la información actualizada del cliente con la nueva foto del DNI.
     * @throws ClienteNotFound Si el cliente no existe en la base de datos.
     */

    @CachePut
    @Override
    @Operation(summary = "Update Cliente DNI Photo", description = "Updates the DNI photo of the specified client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente DNI photo updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cliente not found")
    })
    public ClienteResponse updateDniFoto( @Parameter(description = "Unique client identifier", required = true) String id,
                                          @Parameter(description = "File representing the new DNI photo", required = true) MultipartFile file) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        if (cliente.getFotoDni() != null && !cliente.getFotoDni().isEmpty()) {
            storageImagesService.delete(cliente.getFotoDni());
        }

        String filename = storageImagesService.store(file);
        cliente.setFotoDni(filename);

        var clienteSaved = clienteRepository.save(cliente);

        return clienteMapper.toClienteResponse(clienteSaved, cliente.getUser().getGuid());
    }

    /**
     * Actualiza la foto de perfil de un cliente. Se elimina la foto anterior si ya existe y se guarda la nueva foto.
     *
     * @param id Identificador único del cliente.
     * @param file Archivo de la nueva foto de perfil.
     * @return Devuelve la información actualizada del cliente con la nueva foto de perfil.
     * @throws ClienteNotFound Si el cliente no existe en la base de datos.
     */

    @CachePut
    @Override
    @Operation(summary = "Update Cliente Profile Photo", description = "Updates the profile photo of the specified client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente profile photo updated successfully"),
            @ApiResponse(responseCode = "404", description = "Cliente not found")
    })
    public ClienteResponse updateProfileFoto( @Parameter(description = "Unique client identifier", required = true) String id,
                                              @Parameter(description = "File representing the new profile photo", required = true) MultipartFile file) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        if (cliente.getFotoPerfil() != null && !cliente.getFotoPerfil().isEmpty()) {
            storageImagesService.delete(cliente.getFotoPerfil());
        }

        String filename = storageImagesService.store(file);
        cliente.setFotoPerfil(filename);

        var clienteSaved = clienteRepository.save(cliente);

        return clienteMapper.toClienteResponse(clienteSaved, cliente.getUser().getGuid());
    }

    /**
     * Recupera el catálogo de tipos de cuentas y tipos de tarjetas disponibles para los clientes.
     *
     * @return Devuelve un objeto con la lista de tipos de cuentas y tipos de tarjetas disponibles.
     */

    @Override
    @Operation(summary = "Get Catalogue", description = "Retrieve the types of accounts and card types available for the client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalogue retrieved successfully")
    })
    public ClienteProducto getCatalogue() {
        List<TipoCuenta> tiposCuentas = tipoCuentaRepository.findAll();
        List<TipoCuentaResponseCatalogo> tiposCuentasResponse = new ArrayList<>();
        tiposCuentas.forEach(tipoCuenta -> {
            tiposCuentasResponse.add(tipoCuentaMapper.toTipoCuentaResponseCatalogo(tipoCuenta));
        });

        List<TipoTarjeta> tiposTarjetas = Arrays.asList(TipoTarjeta.values());

        return ClienteProducto.builder()
                .tiposTarjetas(tiposTarjetas)
                .tiposCuentas(tiposCuentasResponse)
                .build();
    }
    /**
     * Limpia la caché de Redis para las claves que coincidan con un prefijo específico.
     *
     * @param cachePrefix Prefijo de las claves que se desean eliminar de la caché de Redis.
     */

    @Operation(summary = "Clear Cache", description = "Clears the Redis cache by specific prefix.")
    private void clearCacheByPrefix(@Parameter(description = "Cache prefix to match and clear", required = true) String cachePrefix) {
        Set<String> keys = redisTemplate.keys(cachePrefix + "::" + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}