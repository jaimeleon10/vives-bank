package org.example.vivesbankproject.movimientos.repositories;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.movimientos.models.Movimiento;
import org.example.vivesbankproject.movimientos.models.Domiciliacion;
import org.example.vivesbankproject.movimientos.models.IngresoDeNomina;
import org.example.vivesbankproject.movimientos.models.PagoConTarjeta;
import org.example.vivesbankproject.movimientos.models.Transferencia;
import org.example.vivesbankproject.utils.generators.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class MovimientoRepositoryTest {

    @Autowired
    private MovimientosRepository movimientosRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Movimiento movimiento;
    private String clienteGuid;

    @BeforeEach
    void setUp() {
        clienteGuid = IdGenerator.generarId();

        movimiento = Movimiento.builder()
                .guid(IdGenerator.generarId())
                .clienteGuid(clienteGuid)
                .domiciliacion(Domiciliacion.builder().guid(IdGenerator.generarId()).build())
                .ingresoDeNomina(IngresoDeNomina.builder().build())
                .pagoConTarjeta(PagoConTarjeta.builder().build())
                .transferencia(Transferencia.builder().build())
                .createdAt(LocalDateTime.now())
                .build();

        mongoTemplate.insert(movimiento);
    }



    @Test
    void findByGuid() {
        Optional<Movimiento> result = movimientosRepository.findByGuid(movimiento.getGuid());

        assertAll(
                () -> assertTrue(result.isPresent(), "Movimiento deberia ser encontrado por el GUID existente"),
                () -> assertEquals(movimiento.getGuid(), result.get().getGuid(), "El movimiento recuperado debe coincidir con el GUID original")
        );
    }

    @Test
    void findByGuid_notFound() {
        String nonExistentGuid = IdGenerator.generarId();

        Optional<Movimiento> result = movimientosRepository.findByGuid(nonExistentGuid);

        assertTrue(result.isEmpty(), "El resultado deberia estar vacio para un GUID inexistente");
    }

    @Test
    void findMovimientosByClienteGuid() {
        System.out.println("Cliente GUID esperado: " + clienteGuid );

        movimientosRepository.save(movimiento);

        Optional<Movimiento> result = movimientosRepository.findByClienteGuid(clienteGuid);

        assertAll(
                () -> assertTrue(result.isPresent(), "Movimiento deberia ser encontrado para el cliente existente"),
                () -> assertEquals(clienteGuid, movimiento.getClienteGuid())
        );
    }

    @Test
    void findMovimientosByClienteGuid_ClienteNoExiste() {
        String nonExistentClienteGuid = IdGenerator.generarId();

        Optional<Movimiento> result = movimientosRepository.findByClienteGuid(nonExistentClienteGuid);

        assertTrue(result.isEmpty(), "El resultado deberia estar vacio para un GUID de cliente inexistente");
    }

    @Test
    void save() {
        Movimiento newMovimiento = Movimiento.builder()
                .guid(IdGenerator.generarId())
                .clienteGuid(IdGenerator.generarId())
                .domiciliacion(Domiciliacion.builder().guid(IdGenerator.generarId()).build())
                .ingresoDeNomina(IngresoDeNomina.builder().build())
                .pagoConTarjeta(PagoConTarjeta.builder().build())
                .transferencia(Transferencia.builder().build())
                .createdAt(LocalDateTime.now())
                .build();

        Movimiento savedMovimiento = movimientosRepository.save(newMovimiento);

        assertAll(
                () -> assertNotNull(savedMovimiento, "El movimiento guardado no debe ser nulo"),
                () -> assertNotNull(savedMovimiento.getId(), "El movimiento guardado debe tener un ID"),
                () -> assertEquals(newMovimiento.getGuid(), savedMovimiento.getGuid(), "El movimiento guardado debe tener el mismo GUID")
        );
    }

    @Test
    void delete() {
        Movimiento savedMovimiento = movimientosRepository.save(movimiento);

        movimientosRepository.delete(savedMovimiento);

        Optional<Movimiento> deletedMovimiento = movimientosRepository.findByGuid(savedMovimiento.getGuid());

        assertTrue(deletedMovimiento.isEmpty(), "El movimiento deberia ser eliminado del repositorio");
    }

    @Test
    void update() {
        Movimiento savedMovimiento = movimientosRepository.save(movimiento);

        savedMovimiento.setIsDeleted(true);

        Movimiento updatedMovimiento = movimientosRepository.save(savedMovimiento);

        assertAll(
                () -> assertTrue(updatedMovimiento.getIsDeleted(), "El flag isDeleted debe ser actualizado")
        );
    }
}
