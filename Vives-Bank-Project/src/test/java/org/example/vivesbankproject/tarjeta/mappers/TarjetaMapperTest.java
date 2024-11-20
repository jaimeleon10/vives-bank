package org.example.vivesbankproject.tarjeta.mappers;

import org.example.vivesbankproject.cliente.models.Cliente;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaRequest;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.example.vivesbankproject.tarjeta.service.TarjetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TarjetaMapperTest {

    @Mock
    private TarjetaService tarjetaService;

    @InjectMocks
    private TarjetaMapper tarjetaMapper;

    private TarjetaRequest tarjetaRequest;
    private Tarjeta tarjeta;
    private TipoTarjeta tipoTarjeta;
    private Cuenta cuenta;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        tarjetaRequest = TarjetaRequest.builder()
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .pin("1234")
                .limiteDiario(5000.0)
                .limiteSemanal(20000.0)
                .limiteMensual(50000.0)
                .tipoTarjeta("DEBITO")
                .cuentaId(UUID.randomUUID())
                .build();

        tipoTarjeta = new TipoTarjeta();
        tipoTarjeta.setNombre(Tipo.DEBITO);

        Cliente cliente = new Cliente();
        cliente.setId(UUID.fromString("d7293a53-c441-4cda-aea2-230cbcf7ec27"));
        cliente.setDni("46911981P");
        cliente.setNombre("Pepe");
        cliente.setApellidos("Gómez");
        cliente.setEmail("pepe.gomez@gmail.com");
        cliente.setTelefono("601938475");
        cliente.setFotoPerfil("https://via.placeholder.com/150");
        cliente.setFotoDni("https://via.placeholder.com/150");

        cuenta = new Cuenta();
        cuenta.setId(UUID.fromString("6c257ab6-e588-4cef-a479-c2f8fcd7379a"));
        cuenta.setIban("ES7302413102733585086708");
        cuenta.setSaldo(1000.0);
        cuenta.setTarjeta(tarjeta);
        cuenta.setIsDeleted(false);

        tarjeta = Tarjeta.builder()
                .id(UUID.randomUUID())
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .pin("1234")
                .limiteDiario(5000.0)
                .limiteSemanal(20000.0)
                .limiteMensual(50000.0)
                .tipoTarjeta(tipoTarjeta)
                .cuenta(cuenta)
                .build();

        cuenta.setTarjeta(tarjeta);
    }

    @Test
    public void testToTarjeta() {
        when(tarjetaService.getTipoTarjetaByNombre(Tipo.DEBITO)).thenReturn(tipoTarjeta);

        Tarjeta mappedTarjeta = tarjetaMapper.toTarjeta(tarjetaRequest);

        assertNotNull(mappedTarjeta);
        assertEquals(tarjetaRequest.getNumeroTarjeta(), mappedTarjeta.getNumeroTarjeta());
        assertEquals(tarjetaRequest.getFechaCaducidad(), mappedTarjeta.getFechaCaducidad());
        assertEquals(tarjetaRequest.getCvv(), mappedTarjeta.getCvv());
        assertEquals(tarjetaRequest.getPin(), mappedTarjeta.getPin());
        assertEquals(tarjetaRequest.getLimiteDiario(), mappedTarjeta.getLimiteDiario());
        assertEquals(tarjetaRequest.getLimiteSemanal(), mappedTarjeta.getLimiteSemanal());
        assertEquals(tarjetaRequest.getLimiteMensual(), mappedTarjeta.getLimiteMensual());
        assertEquals(tipoTarjeta, mappedTarjeta.getTipoTarjeta());
    }

    @Test
    public void testToRequest() {
        TarjetaRequest mappedRequest = tarjetaMapper.toRequest(tarjeta);

        assertNotNull(mappedRequest);
        assertEquals(tarjeta.getNumeroTarjeta(), mappedRequest.getNumeroTarjeta());
        assertEquals(tarjeta.getFechaCaducidad(), mappedRequest.getFechaCaducidad());
        assertEquals(tarjeta.getCvv(), mappedRequest.getCvv());
        assertEquals(tarjeta.getPin(), mappedRequest.getPin());
        assertEquals(tarjeta.getLimiteDiario(), mappedRequest.getLimiteDiario());
        assertEquals(tarjeta.getLimiteSemanal(), mappedRequest.getLimiteSemanal());
        assertEquals(tarjeta.getLimiteMensual(), mappedRequest.getLimiteMensual());
        assertEquals(tarjeta.getTipoTarjeta().getNombre().name(), mappedRequest.getTipoTarjeta());
        assertEquals(tarjeta.getCuenta().getId(), mappedRequest.getCuentaId());
    }
}
