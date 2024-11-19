package org.example.vivesbankproject.cuenta.services;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.exceptions.CuentaNotFound;
import org.example.vivesbankproject.cuenta.mappers.CuentaMapper;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {
    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuentaTest;
    private Cliente clienteTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente();
        clienteTest.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        clienteTest.setDni("76742083F");
        clienteTest.setNombre("Juan");
        clienteTest.setApellidos("Pérez");
        clienteTest.setEmail("juan.perez@gmail.com");
        clienteTest.setTelefono("678349823");
        clienteTest.setFotoPerfil("https://via.placeholder.com/150");
        clienteTest.setFotoDni("https://via.placeholder.com/150");

        tarjetaTest = new Tarjeta();
        tarjetaTest.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(100.0);
        tarjetaTest.setLimiteSemanal(200.0);
        tarjetaTest.setLimiteMensual(500.0);
        tarjetaTest.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(2.0);

        cuentaTest = new Cuenta();
        cuentaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(1000.0);
        cuentaTest.setCliente(clienteTest);
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void getAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nombre").ascending());

        Page<Cuenta> cuentaPage = new PageImpl<>(List.of(cuentaTest), pageable, 1);

        when(cuentaRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cuentaPage);

        var result = cuentaService.getAll(Optional.of(cuentaTest.getIban()), Optional.of(cuentaTest.getSaldo()), Optional.of(cuentaTest.getCliente()), Optional.of(cuentaTest.getTarjeta()), Optional.of(cuentaTest.getTipoCuenta()), pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getContent().size()),
                () -> assertTrue(result.getContent().contains(cuentaTest)),
                () -> assertEquals("ES9120804243448487618583", result.getContent().getFirst().getIban()),
                () -> assertEquals(1000.0, result.getContent().getFirst().getSaldo()),
                () -> assertEquals(clienteTest, result.getContent().getFirst().getCliente()),
                () -> assertEquals(tipoCuentaTest, result.getContent().getFirst().getTipoCuenta()),
                () -> assertEquals(tarjetaTest, result.getContent().getFirst().getTarjeta()),
                () -> assertFalse(result.getContent().getFirst().getIsDeleted())
        );

        verify(cuentaRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getById() {
        UUID idCuenta = cuentaTest.getId();
        Optional<Cuenta> expectedCuenta = Optional.of(new Cuenta());
        when(cuentaRepository.findById(idCuenta)).thenReturn(expectedCuenta);

        Optional<Cuenta> resultCuenta = cuentaService.getById(idCuenta);

        assertEquals(expectedCuenta, resultCuenta);

        verify(cuentaRepository).findById(idCuenta);
    }

    @Test
    void getByIdNotFound() {
        UUID idCuenta = UUID.fromString("4182d617-ec89-4fbc-be95-85e461778700");
        when(cuentaRepository.findById(UUID.fromString("4182d617-ec89-4fbc-be95-85e461778700"))).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.getById(idCuenta));

        verify(cuentaRepository).findById(idCuenta);
    }

    @Test
    void save() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("68aa261a-56d7-4e5f-a7b9-1b6e7b3a04a4"));
        cliente.setDni("44889646V");
        cliente.setNombre("Jesus");
        cliente.setApellidos("Jimenez");
        cliente.setEmail("jesus.jimenez@gmail.com");
        cliente.setTelefono("623479558");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("7b498e86-5197-4e05-9361-3da894b62353"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(987);
        tarjeta.setPin("0987");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);

        TipoCuenta tipoCuenta = new TipoCuenta();
        tipoCuenta.setNombre("normal");
        tipoCuenta.setInteres(2.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES0901869615019736267715");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(cliente);
        cuenta.setTipoCuenta(tipoCuenta);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        when(cuentaRepository.save(cuenta)).thenReturn(cuenta);

        var result = cuentaService.save(cuenta);

        assertAll(
                () -> assertEquals(cuenta.getId(), result.getId()),
                () -> assertEquals(cuenta.getIban(), result.getIban()),
                () -> assertEquals(cuenta.getSaldo(), result.getSaldo()),
                () -> assertEquals(cuenta.getCliente(), result.getCliente()),
                () -> assertEquals(cuenta.getTipoCuenta(), result.getTipoCuenta()),
                () -> assertEquals(cuenta.getTarjeta(), result.getTarjeta()),
                () -> assertFalse(result.getIsDeleted())
        );

        verify(cuentaRepository, times(1)).save(cuenta);
    }

    @Test
    void update() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        cliente.setDni("46911981P");
        cliente.setNombre("Pepe");
        cliente.setApellidos("Gómez");
        cliente.setEmail("pepe.gomez@gmail.com");
        cliente.setTelefono("601938475");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjeta.setNumeroTarjeta("4009156782194826");
        tarjeta.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjeta.setCvv(456);
        tarjeta.setPin("4567");
        tarjeta.setLimiteDiario(100.0);
        tarjeta.setLimiteSemanal(200.0);
        tarjeta.setLimiteMensual(500.0);
        tarjeta.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("ahorro");
        tipoCuentaTest.setInteres(3.0);

        Cuenta cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(1000.0);
        cuenta.setCliente(cliente);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        UUID idCuenta = cuenta.getId();

        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        Cuenta resultPedido = cuentaService.update(idCuenta, cuenta);

        assertAll(
                () -> assertEquals(cuenta, resultPedido)
        );

        verify(cuentaRepository).findById(idCuenta);
        verify(cuentaRepository).save(any(Cuenta.class));
    }

    @Test
    void updateNotFound() {
        UUID idCuenta = UUID.fromString("4182d617-ec89-4fbc-be95-85e461778700");
        Cuenta cuenta = new Cuenta();
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.update(idCuenta, cuenta));

        verify(cuentaRepository).findById(idCuenta);
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void delete() {
        UUID idCuenta = UUID.randomUUID();
        Cuenta cuentaToDelete = new Cuenta();
        
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.of(cuentaToDelete));

        cuentaService.delete(idCuenta);

        verify(cuentaRepository, times(1)).findById(idCuenta);
        verify(cuentaMapper, times(1)).toCuentaUpdate(cuentaToDelete);
    }

    @Test
    void deleteNotFound() {
        UUID idCuenta = cuentaTest.getId();
        when(cuentaRepository.findById(idCuenta)).thenReturn(Optional.empty());

        assertThrows(CuentaNotFound.class, () -> cuentaService.delete(idCuenta));

        verify(cuentaRepository).findById(idCuenta);
        verify(cuentaRepository, times(0)).deleteById(idCuenta);
    }
}