package org.example.vivesbankproject.tarjeta.mappers;

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

import java.math.BigDecimal;
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
                .limiteDiario(BigDecimal.valueOf(5000.0))
                .limiteSemanal(BigDecimal.valueOf(20000.0))
                .limiteMensual(BigDecimal.valueOf(50000.0))
                .tipoTarjeta("DEBITO")
                .cuentaId(UUID.randomUUID())
                .build();

        tipoTarjeta = new TipoTarjeta();
        tipoTarjeta.setNombre(Tipo.DEBITO);

        tarjeta = Tarjeta.builder()
                .id(UUID.randomUUID())
                .numeroTarjeta("1234567890123456")
                .fechaCaducidad(LocalDate.of(2025, 12, 31))
                .cvv(123)
                .pin("1234")
                .limiteDiario(BigDecimal.valueOf(5000.0))
                .limiteSemanal(BigDecimal.valueOf(20000.0))
                .limiteMensual(BigDecimal.valueOf(50000.0))
                .tipoTarjeta(tipoTarjeta)
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
    }
}
