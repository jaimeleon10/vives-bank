package org.example.vivesbankproject.cliente.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.cliente.dto.ClienteInfoResponse;
import org.example.vivesbankproject.cliente.dto.ClienteRequest;
import org.example.vivesbankproject.cliente.dto.ClienteResponse;
import org.example.vivesbankproject.cliente.exceptions.ClienteNotFound;
import org.example.vivesbankproject.cliente.mappers.ClienteMapper;
import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cliente.repositories.ClienteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteMapper clienteMapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository, CuentaRepository cuentaRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
        this.clienteMapper = clienteMapper;
    }

    @Override
    public Page<ClienteResponse> findAll(Optional<String> nombre, Optional<String> dni, Optional<String> email, Optional<String> telefono, Pageable pageable) {
        log.info("Buscando todos los clientes con filtrado por: {}, {}, {} con borrado: {}", nombre, dni, email, telefono);

        Specification<Cliente> specNombreCliente = (root, query, criteriaBuilder) ->
                nombre.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specDniCliente = (root, query, criteriaBuilder) ->
                dni.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("dni")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specEmailCliente = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> specteleCliente = (root, query, criteriaBuilder) ->
                telefono.map(m -> criteriaBuilder.equal(root.get("telefono"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Cliente> criterio = Specification.where(specNombreCliente)
                .and(specDniCliente)
                .and(specEmailCliente)
                .and(specteleCliente);

        return clienteRepository.findAll(criterio, pageable).map(clienteMapper::toClienteResponse);
    }

    /*@Override
    public ClienteInfoResponse findById(UUID id) {
        log.info("Buscando cliente por id: {}", id);

        var cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFound(id));
        var cuenta = cuentaRepository.findCuentaIdsByIdCliente(id).stream().map(p -> p.getId().toHexString()).toList();

        return clienteMapper.toClienteInfoResponse(cliente, cuenta);
    }

    @Override
    public ClienteResponse save(ClienteRequest clienteRequest) {
        log.info("Guardando cliente: {}", clienteRequest);
        clienteRepository.findByCliente(clienteRequest.getNombre(), clienteRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Ya existe un usuario con el username o email introducido");
                });
        return clienteMapper.toClienteResponse(clienteRepository.save(clienteMapper.toCliente(clienteRequest)));
    }

    @Override
    public ClienteResponse update(UUID id, ClienteRequest clienteRequest) {
        log.info("Actualizando cliente: {}", clienteRequest);
       clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFound(id));
        clienteRepository.findByClienteEqualsIgnoreCaseOrEmailEqualsIgnoreCase(clienteRequest.getUsername(), clienteRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("cliente encontrado: " + u.getId() + " Mi id: " + id);
                        throw new ClienteNameOrEmailExists("Ya existe un cliente con el username o email introducido");
                    }
                });
        return clienteMapper.toClienteResponse(clienteRepository.save(clienteMapper.toCliente(clienteRequest, id)));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Borrando cliente por id: {}", id);
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ClienteNotFound(id));
        if (cuentaRepository.existsByIdCliente(id)) {
            log.info("Borrado lógico de cliente por id: {}", id);
            clienteRepository.updateIsDeletedToTrueById(id);
        } else {
            log.info("Borrado físico de cliente por id: {}", id);
           clienteRepository.delete(cliente);
        }
    }*/
}