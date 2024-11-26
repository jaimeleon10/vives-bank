package org.example.vivesbankproject.movimientos.repositories;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.utils.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class MovimientoRepositoryTest {

    @Autowired
    private MovimientosRepository movimientosRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Environment env;

    private String clienteId;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        clienteId = IdGenerator.generarId();
        Cliente cliente = new Cliente();
        cliente.setGuid(clienteId);

        movimiento = new Movimiento();
        movimiento.setCliente(cliente);

        mongoTemplate.insert(movimiento);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(Movimiento.class);
    }

    @Test
    void findMovimientosByClienteId_shouldReturnMovimientos_whenClienteExists() {
        System.out.println("Saved movimientos: " + mongoTemplate.findAll(Movimiento.class));

        Optional<Movimiento> result = movimientosRepository.findMovimientosByClienteId(clienteId);

        assertAll(
                () -> assertTrue(result.isPresent(), "El resultado debería estar presente"),
                () -> assertEquals(clienteId, result.get().getCliente().getGuid(), "El ID del cliente debería coincidir")
        );
    }

    @Test
    void findMovimientosByClienteId_shouldReturnEmpty_whenClienteDoesNotExist() {
        String nonExistentClienteId = IdGenerator.generarId();

        Optional<Movimiento> result = movimientosRepository.findMovimientosByClienteId(nonExistentClienteId);

        assertTrue(result.isEmpty(), "El resultado debería estar vacío para un cliente inexistente");
    }

    @Test
    void verifyEmbeddedMongoDB() {
        assertAll(
                () -> assertTrue(mongoTemplate.getDb().getName().startsWith("banco-dev"), "El nombre de la base de datos debería empezar con 'test'"),
                () -> assertNull(env.getProperty("spring.data.mongodb.uri"), "La URI de MongoDB debería ser null para la base de datos embebida")
        );
    }
}