package org.example.vivesbankproject.cliente.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.*;
import org.example.vivesbankproject.cliente.exceptions.*;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.mappers.TipoCuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.mappers.TarjetaMapper;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
import org.example.vivesbankproject.users.exceptions.UserNotFoundException;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@CacheConfig(cacheNames={"cliente"})
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CuentaMapper cuentaMapper;
    private final CuentaRepository cuentaRepository;
    private final TipoCuentaMapper tipoCuentaMapper;
    private final TarjetaMapper tarjetaMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, UserMapper userMapper, UserRepository userRepository, CuentaMapper cuentaMapper, CuentaRepository cuentaRepository, TipoCuentaMapper tipoCuentaMapper, TarjetaMapper tarjetaMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.cuentaMapper = cuentaMapper;
        this.cuentaRepository = cuentaRepository;
        this.tipoCuentaMapper = tipoCuentaMapper;
        this.tarjetaMapper = tarjetaMapper;
    }

    @Override
    public Page<ClienteResponse> getAll(Optional<String> dni, Optional<String> nombre, Optional<String> apellidos, Optional<String> email, Optional<String> telefono, Pageable pageable) {
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

    @Override
    @Cacheable
    public ClienteResponse getById(String id) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(() -> new ClienteNotFound(id));
        String userId = cliente.getUser().getGuid();

        return clienteMapper.toClienteResponse(cliente, userId);
    }

    @Override
    @CachePut
    public ClienteResponse save(ClienteRequestSave clienteRequestSave) {
        // Buscamos si existe algún cliente con el usuario adjunto ya asignado
        if (clienteRepository.existsByUserGuid(clienteRequestSave.getUserId())) {
            throw new ClienteUserAlreadyAssigned(clienteRequestSave.getUserId());
        }

        // Buscamos si existe el usuario por la id ajuntada en el cliente request
        var usuarioExistente = userRepository.findByGuid(clienteRequestSave.getUserId()).orElseThrow(
                () -> new UserNotFoundById(clienteRequestSave.getUserId())
        );

        // Mapeamos a cliente con el cliente request, el usuario existente y las cuentas existentes
        var cliente = clienteMapper.toCliente(clienteRequestSave, usuarioExistente);

        // Validamos datos (dni, email y teléfono) existentes
        validarClienteExistente(cliente);

        // Guardamos el cliente y lo mapeamos a response para devolverlo
        var clienteSaved = clienteRepository.save(cliente);
        return clienteMapper.toClienteResponse(clienteSaved, usuarioExistente.getGuid());
    }

    @Override
    @CachePut
    public ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate) {
        // Buscamos si existe el cliente con la el parámetro id
        var clienteExistente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        // Buscamos si existe el usuario por el parámetro id ajuntado en el cliente request
        var usuarioExistente = userRepository.findByGuid(clienteRequestUpdate.getUserId()).orElseThrow(
                () -> new UserNotFoundById(clienteRequestUpdate.getUserId())
        );

        // Buscamos si existe algún cliente con el usuario adjunto ya asignado
        if (clienteRepository.existsByUserGuid(clienteRequestUpdate.getUserId())) {
            throw new ClienteUserAlreadyAssigned(clienteRequestUpdate.getUserId());
        }

        // Validamos si el nuevo email y telefono introducido existe en caso de que sea distinto del existente
        if (!Objects.equals(clienteRequestUpdate.getTelefono(), clienteExistente.getTelefono())) {
            if (clienteRepository.findByTelefono(clienteRequestUpdate.getTelefono()).isPresent()) {
                throw new ClienteExistsByTelefono(clienteRequestUpdate.getTelefono());
            }
        }
        if (!Objects.equals(clienteRequestUpdate.getEmail(), clienteExistente.getEmail())) {
            if (clienteRepository.findByEmail(clienteRequestUpdate.getNombre()).isPresent()) {
                throw new ClienteExistsByEmail(clienteRequestUpdate.getEmail());
            }
        }

        // Guardamos el cliente mapeado a update
        var clienteSave = clienteRepository.save(clienteMapper.toClienteUpdate(clienteRequestUpdate, clienteExistente, usuarioExistente));

        // Devolvemos el cliente response con los datos necesarios
        return clienteMapper.toClienteResponse(clienteSave, usuarioExistente.getGuid());
    }

    @Override
    @CacheEvict
    @Transactional
    public void deleteById(String id) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );
        cliente.setIsDeleted(true);
        clienteRepository.save(cliente);
    }

    @Override
    @Cacheable(key = "#guid") // Añadimos caché para mejorar el rendimiento en llamadas repetidas
    public ClienteResponse getUserByGuid(String guid) {
        log.info("Buscando cliente por user guid: {}", guid);

        // Buscamos el cliente directamente por el userGuid
        var cliente = clienteRepository.findByUserGuid(guid)
                .orElseThrow(() -> new ClienteNotFound("No se encontró cliente para el usuario con guid: " + guid));

        // Obtenemos el usuario asociado al cliente
        var usuarioExistente = cliente.getUser();
        if (usuarioExistente == null) {
            throw new UserNotFoundById(guid);
        }

        // Obtenemos las cuentas del cliente
        Set<CuentaForClienteResponse> cuentasResponse = cliente.getCuentas().stream()
                .map(cuenta -> cuentaMapper.toCuentaForClienteResponse(
                        cuenta,
                        tipoCuentaMapper.toTipoCuentaResponse(cuenta.getTipoCuenta()),
                        tarjetaMapper.toTarjetaResponse(cuenta.getTarjeta())))
                .collect(Collectors.toSet());

        return clienteMapper.toClienteResponse(
                cliente,
                userMapper.toUserResponse(usuarioExistente),
                cuentasResponse
        );
    }

    private void validarClienteExistente(Cliente cliente) {
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
}