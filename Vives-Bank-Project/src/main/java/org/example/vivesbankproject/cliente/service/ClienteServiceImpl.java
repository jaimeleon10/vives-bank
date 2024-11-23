package org.example.vivesbankproject.cliente.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteCuentaRequest;
import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.*;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaResponse;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.exceptions.UserNotFoundById;
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

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, UserMapper userMapper, UserRepository userRepository, CuentaMapper cuentaMapper, CuentaRepository cuentaRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.cuentaMapper = cuentaMapper;
        this.cuentaRepository = cuentaRepository;
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
            UserResponse userResponse = userMapper.toUserResponse(cliente.getUser());
            Set<CuentaResponse> cuentasResponse = cliente.getCuentas().stream()
                   .map(cuentaMapper::toCuentaResponse)
                   .collect(Collectors.toSet());

            return clienteMapper.toClienteResponse(cliente, userResponse, cuentasResponse);
        });
    }

    @Override
    @Cacheable(key = "#id")
    public ClienteResponse getById(String id) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(() -> new ClienteNotFound(id));
        var user = userMapper.toUserResponse(cliente.getUser());
        Set<CuentaResponse> cuentasResponse = cliente.getCuentas().stream()
               .map(cuentaMapper::toCuentaResponse)
               .collect(Collectors.toSet());

        return clienteMapper.toClienteResponse(cliente, user, cuentasResponse);
    }

    @Override
    @CachePut(key = "#result.guid")
    public ClienteResponse save(ClienteRequestSave clienteRequestSave) {
        // Buscamos si existe algún cliente con el usuario adjunto ya asignado
        if (clienteRepository.existsByUserGuid(clienteRequestSave.getUserId())) {
            throw new ClienteUserAlreadyAssigned(clienteRequestSave.getUserId());
        }

        // Buscamos si las cuentas existen
        clienteRequestSave.getCuentasIds().forEach(cuentaGuid -> {
                if (cuentaRepository.findByGuid(cuentaGuid).isEmpty()) {
                    throw new CuentaNotFound(cuentaGuid);
                }
            }
        );

        // Buscamos si las cuentas adjuntadas están asignadas a algún cliente y en ese caso lanzamos excepcion
        List<Cuenta> cuentas = clienteRepository.findCuentasAsignadas(clienteRequestSave.getCuentasIds());

        if (!cuentas.isEmpty()) {
            String cuentasAsignadas = cuentas.stream()
                    .map(Cuenta::getGuid)
                    .collect(Collectors.joining(", "));

            throw new ClienteCuentasAlreadyAssigned(cuentasAsignadas);
        }

        // Buscamos si existe el usuario por la id ajuntada en el cliente request
        var usuarioExistente = userRepository.findByGuid(clienteRequestSave.getUserId()).orElseThrow(
                () -> new UserNotFoundById(clienteRequestSave.getUserId())
        );

        // Creamos listado de cuentas
        Set<Cuenta> cuentasExistentes = new HashSet<>();

        // Buscamos si existen los ids de las cuentas adjuntadas en el cliente request y si existen las guardamos en el listado de cuentas
        if (!clienteRequestSave.getCuentasIds().isEmpty()) {
            for (String cuentaId : clienteRequestSave.getCuentasIds()) {
                Cuenta cuenta = cuentaRepository.findByGuid(cuentaId).orElseThrow(
                        () -> new CuentaNotFound(cuentaId)
                );
                cuentasExistentes.add(cuenta);
            }
        }

        // Mapeamos a cliente con el cliente request, el usuario existente y las cuentas existentes
        var cliente = clienteMapper.toCliente(clienteRequestSave, usuarioExistente, cuentasExistentes);

        // Validamos datos (dni, email y teléfono) existentes
        validarClienteExistente(cliente);

        // Mapeamos el Set<Cuenta> a Set<CuentaResponse>
        Set<CuentaResponse> cuentasResponse = cuentasExistentes.stream()
                .map(cuentaMapper::toCuentaResponse)
                .collect(Collectors.toSet());

        // Guardamos el cliente y lo mapeamos a response para devolverlo
        return clienteMapper.toClienteResponse(clienteRepository.save(cliente), userMapper.toUserResponse(usuarioExistente), cuentasResponse);
    }

    @Override
    @CachePut(key = "#result.guid")
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

        // Mapeamos el Set<Cuenta> a Set<CuentaResponse>
        Set<CuentaResponse> cuentasResponse = clienteExistente.getCuentas().stream()
                .map(cuentaMapper::toCuentaResponse)
                .collect(Collectors.toSet());

        // Guardamos el cliente mapeado a update
        var clienteSave = clienteRepository.save(clienteMapper.toClienteUpdate(clienteRequestUpdate, clienteExistente, usuarioExistente));

        // Devolvemos el cliente response con los datos necesarios
        return clienteMapper.toClienteResponse(clienteSave, userMapper.toUserResponse(usuarioExistente), cuentasResponse);
    }

    @Override
    public ClienteResponse addCuentas(String id, ClienteCuentaRequest clienteCuentaRequest) {
        // Buscamos si existe el cliente con la el parámetro id
        var clienteExistente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        // Buscamos si las cuentas existen
        clienteCuentaRequest.getCuentasIds().forEach(cuentaGuid -> {
                    if (cuentaRepository.findByGuid(cuentaGuid).isEmpty()) {
                        throw new CuentaNotFound(cuentaGuid);
                    }
                }
        );

        // Buscamos si las cuentas adjuntadas están asignadas a algún cliente y en ese caso lanzamos excepcion
        List<Cuenta> cuentas = clienteRepository.findCuentasAsignadas(clienteCuentaRequest.getCuentasIds());

        if (!cuentas.isEmpty()) {
            String cuentasAsignadas = cuentas.stream()
                    .map(Cuenta::getGuid)
                    .collect(Collectors.joining(", "));

            throw new ClienteCuentasAlreadyAssigned(cuentasAsignadas);
        }

        // Buscamos las cuentas en el Set<Cuenta> de clienteCuentaRequest y validamos si existen. En ese caso añadimos al listado.
        Set<Cuenta> cuentasExistentes = clienteExistente.getCuentas();
        for (String cuentaId : clienteCuentaRequest.getCuentasIds()) {
            Cuenta cuenta = cuentaRepository.findByGuid(cuentaId).orElseThrow(
                    () -> new CuentaNotFound(cuentaId)
            );
            cuentasExistentes.add(cuenta);
        }

        // Añadimos las cuentas al Set<Cuenta> del cliente existente
        clienteExistente.setCuentas(cuentasExistentes);

        // Guardamos el cliente con las nuevas cuentas
        var clienteSaved = clienteRepository.save(clienteExistente);

        // Mapeamos el Set<Cuenta> a Set<CuentaResponse>
        Set<CuentaResponse> cuentasResponse = clienteExistente.getCuentas().stream()
                .map(cuentaMapper::toCuentaResponse)
                .collect(Collectors.toSet());

        // Devolvemos el cliente como response mapeando los datos necesarios
        return clienteMapper.toClienteResponse(clienteSaved, userMapper.toUserResponse(clienteExistente.getUser()), cuentasResponse);
    }

    @Override
    public ClienteResponse deleteCuentas(String id, ClienteCuentaRequest clienteCuentaRequest) {
        // Buscamos si existe el cliente con la el parámetro id
        var clienteExistente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );

        // Buscamos las cuentas en el Set<Cuenta> de clienteCuentaRequest y validamos si existen. En ese caso borramos del listado.
        Set<Cuenta> cuentasExistentes = clienteExistente.getCuentas();
        for (String cuentaId : clienteCuentaRequest.getCuentasIds()) {
            Cuenta cuenta = cuentaRepository.findByGuid(cuentaId).orElseThrow(
                    () -> new CuentaNotFound(cuentaId)
            );
            cuentasExistentes.remove(cuenta);
        }

        // Añadimos las cuentas al Set<Cuenta> del cliente existente
        clienteExistente.setCuentas(cuentasExistentes);

        // Guardamos el cliente con las nuevas cuentas
        var clienteSaved = clienteRepository.save(clienteExistente);

        // Mapeamos el Set<Cuenta> a Set<CuentaResponse>
        Set<CuentaResponse> cuentasResponse = clienteExistente.getCuentas().stream()
                .map(cuentaMapper::toCuentaResponse)
                .collect(Collectors.toSet());

        // Devolvemos el cliente como response mapeando los datos necesarios
        return clienteMapper.toClienteResponse(clienteSaved, userMapper.toUserResponse(clienteExistente.getUser()), cuentasResponse);
    }

    @Override
    @CacheEvict(key = "#id")
    @Transactional
    public void deleteById(String id) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );
        cliente.setIsDeleted(true);
        clienteRepository.save(cliente);
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