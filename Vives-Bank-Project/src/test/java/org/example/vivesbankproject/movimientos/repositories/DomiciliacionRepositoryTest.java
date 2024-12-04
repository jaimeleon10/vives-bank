package org.example.vivesbankproject.movimientos.repositories;

import org.bson.types.ObjectId;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.Periodicidad;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class DomiciliacionRepositoryTest {

    @Autowired
    private DomiciliacionRepository domiciliacionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Domiciliacion domiciliacion;
    private String clienteGuid;

    @BeforeEach
    void setUp() {
        clienteGuid = IdGenerator.generarId();

        domiciliacion = Domiciliacion.builder()
                .guid(IdGenerator.generarId())
                .clienteGuid(clienteGuid)
                .ibanOrigen("ES1234567890123456789012")
                .ibanDestino("ES9876543210987654321098")
                .cantidad(BigDecimal.valueOf(100.50))
                .nombreAcreedor("Test Acreedor")
                .periodicidad(Periodicidad.MENSUAL)
                .activa(true)
                .build();

        mongoTemplate.insert(domiciliacion);
    }

    @Test
    void findByGuid_deberiaDevolverDomiciliacion_cuandoGuidExiste() {
        Optional<Domiciliacion> result = domiciliacionRepository.findByGuid(domiciliacion.getGuid());

        assertAll(
                () -> assertTrue(result.isPresent(), "Domiciliacion deberia ser encontrada por el GUID existente"),
                () -> assertEquals(domiciliacion.getGuid(), result.get().getGuid(), "La domiciliacion recuperada debe coincidir con el GUID original")
        );
    }

    @Test
    void findByGuid_deberiaDevolverVacio_cuandoGuidNoExiste() {
        String nonExistentGuid = IdGenerator.generarId();

        Optional<Domiciliacion> result = domiciliacionRepository.findByGuid(nonExistentGuid);

        assertTrue(result.isEmpty(), "El resultado deberia estar vacio para un GUID inexistente");
    }

    @Test
    void findByClienteGuid_deberiaDevolverDomiciliacion_cuandoClienteExiste() {
        Optional<Domiciliacion> result = domiciliacionRepository.findByClienteGuid(clienteGuid);

        assertAll(
                () -> assertTrue(result.isPresent(), "Domiciliacion deberia ser encontrada para el cliente existente"),
                () -> assertEquals(clienteGuid, result.get().getClienteGuid(), "La domiciliacion recuperada debe tener el GUID del cliente correcto")
        );
    }

    @Test
    void findByClienteGuid_deberiaDevolverVacio_cuandoClienteNoExiste() {
        String nonExistentClienteGuid = IdGenerator.generarId();

        Optional<Domiciliacion> result = domiciliacionRepository.findByClienteGuid(nonExistentClienteGuid);

        assertTrue(result.isEmpty(), "El resultado deberia estar vacio para un GUID de cliente inexistente");
    }

    @Test
    void save_deberiaPersistirDomiciliacion() {
        Domiciliacion newDomiciliacion = Domiciliacion.builder()
                .guid(IdGenerator.generarId())
                .clienteGuid(IdGenerator.generarId())
                .ibanOrigen("ES1111222233334444555566")
                .ibanDestino("ES9999888877776666555544")
                .cantidad(BigDecimal.valueOf(250.75))
                .nombreAcreedor("Nuevo Acreedor")
                .periodicidad(Periodicidad.MENSUAL)
                .activa(true)
                .build();

        Domiciliacion savedDomiciliacion = domiciliacionRepository.save(newDomiciliacion);

        assertAll(
                () -> assertNotNull(savedDomiciliacion, "La domiciliacion guardada no debe ser nula"),
                () -> assertNotNull(savedDomiciliacion.getId(), "La domiciliacion guardada debe tener un ID"),
                () -> assertEquals(newDomiciliacion.getGuid(), savedDomiciliacion.getGuid(), "La domiciliacion guardada debe tener el mismo GUID")
        );
    }

    @Test
    void delete_deberiaEliminarDomiciliacion() {
        Domiciliacion savedDomiciliacion = domiciliacionRepository.save(domiciliacion);

        domiciliacionRepository.delete(savedDomiciliacion);

        Optional<Domiciliacion> deletedDomiciliacion = domiciliacionRepository.findByGuid(savedDomiciliacion.getGuid());

        assertTrue(deletedDomiciliacion.isEmpty(), "La domiciliacion deberia ser eliminada del repositorio");
    }

    @Test
    void update_deberiaModificarDomiciliacionExistente() {
        Domiciliacion savedDomiciliacion = domiciliacionRepository.save(domiciliacion);

        savedDomiciliacion.setCantidad(BigDecimal.valueOf(200.00));
        savedDomiciliacion.setActiva(false);

        Domiciliacion updatedDomiciliacion = domiciliacionRepository.save(savedDomiciliacion);

        assertAll(
                () -> assertEquals(BigDecimal.valueOf(200.00), updatedDomiciliacion.getCantidad(), "La cantidad debe ser actualizada"),
                () -> assertFalse(updatedDomiciliacion.getActiva(), "El flag activa debe ser actualizado")
        );
    }
}
