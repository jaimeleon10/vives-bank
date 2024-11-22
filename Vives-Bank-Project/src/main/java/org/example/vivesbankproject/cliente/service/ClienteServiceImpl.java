package org.example.vivesbankproject.cliente.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteRequestSave;
import org.example.vivesbankproject.cliente.dto.ClienteRequestUpdate;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByDni;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByEmail;
import org.example.vivesbankproject.cliente.exceptions.ClienteExistsByTelefono;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames={"cliente"})
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UserMapper userMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, UserMapper userMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.userMapper = userMapper;
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

        // TODO
        var user = userMapper(clientePage.getContent().get().getUser());

        return clientePage.map(clienteMapper.toClienteResponse(clientePage.getContent().get(), user));
    }

    @Override
    @Cacheable(key = "#id")
    public ClienteResponse getById(String id) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(() -> new ClienteNotFound(id));
        var user = userMapper.toUserResponse(cliente.getUser());
        return clienteMapper.toClienteResponse(cliente, user);
    }

    @Override
    @CachePut(key = "#result.guid")
    public ClienteResponse save(ClienteRequestSave clienteRequestSave) {
        var clienteForSave = clienteMapper.toCliente(clienteRequestSave);
        validarClienteExistente(clienteForSave);
        var clienteSaved = clienteRepository.save(clienteForSave);
        var user = userMapper.toUserResponse(clienteSaved.getUser());
        return clienteMapper.toClienteResponse(clienteSaved, user);
    }

    @Override
    @CachePut(key = "#result.guid")
    public ClienteResponse update(String id, ClienteRequestUpdate clienteRequestUpdate) {
        var cliente = clienteRepository.findByGuid(id).orElseThrow(
                () -> new ClienteNotFound(id)
        );
        var clienteForUpdate = clienteMapper.toClienteUpdate(clienteRequestUpdate, cliente);
        validarClienteExistente(clienteForUpdate);
        var clienteUpdated = clienteRepository.save(clienteForUpdate);
        var user = userMapper.toUserResponse(cliente.getUser());
        return clienteMapper.toClienteResponse(clienteUpdated, user);
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