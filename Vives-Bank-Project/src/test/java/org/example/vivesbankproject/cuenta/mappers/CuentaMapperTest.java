package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.CuentaRequest;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.Tipo;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CuentaMapperTest {
    private final CuentaMapper mapper = new CuentaMapper();

    private Cuenta cuentaTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;

    @BeforeEach
    void setUp() {
        tarjetaTest = new Tarjeta();
        tarjetaTest.setId(UUID.fromString("921f6b86-695d-4361-8905-365d97691024"));
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaTest.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaTest.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaTest.setTipoTarjeta(TipoTarjeta.builder().nombre(Tipo.valueOf("DEBITO")).build());

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        cuentaTest = new Cuenta();
        cuentaTest.setId(UUID.fromString("12d45756-3895-49b2-90d3-c4a12d5ee081"));
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void toCuentaResponse() {
        var res = mapper.toCuentaResponse(cuentaTest);

        assertAll(
                () -> assertEquals(cuentaTest.getId(), res.getId()),
                () -> assertEquals(cuentaTest.getIban(), res.getIban()),
                () -> assertEquals(cuentaTest.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuentaTest.getTipoCuenta().getNombre(), res.getTipoCuenta().getNombre()),
                () -> assertEquals(cuentaTest.getTarjeta().getNumeroTarjeta(), res.getTarjeta().getNumeroTarjeta()),
                () -> assertFalse(res.getIsDeleted())
        );
    }

    @Test
    void toCuenta() {
        CuentaRequest cuentaRequest = new CuentaRequest();
        cuentaRequest.setIban("ES9120804243448487618583");
        cuentaRequest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaRequest.setTipoCuenta(tipoCuentaTest);
        cuentaRequest.setTarjeta(tarjetaTest);
        cuentaRequest.setIsDeleted(false);

        var res = mapper.toCuenta(cuentaRequest);

        assertAll(
                () -> assertEquals(cuentaRequest.getIban(), res.getIban()),
                () -> assertEquals(cuentaRequest.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuentaRequest.getTipoCuenta().getNombre(), res.getTipoCuenta().getNombre()),
                () -> assertEquals(cuentaRequest.getTarjeta().getNumeroTarjeta(), res.getTarjeta().getNumeroTarjeta()),
                () -> assertFalse(res.getIsDeleted())
        );
    }
}