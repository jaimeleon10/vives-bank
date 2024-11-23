package org.example.vivesbankproject.cuenta.mappers;

import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequest;
import org.example.vivesbankproject.cuenta.dto.cuenta.CuentaRequestUpdate;
import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaResponse;
import org.example.vivesbankproject.cuenta.models.Cuenta;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.tarjeta.dto.TarjetaResponse;
import org.example.vivesbankproject.tarjeta.models.Tarjeta;
import org.example.vivesbankproject.tarjeta.models.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CuentaMapperTest {
    private final CuentaMapper mapper = new CuentaMapper();

    private Cuenta cuentaTest;
    private Tarjeta tarjetaTest;
    private TipoCuenta tipoCuentaTest;
    private TipoCuentaResponse tipoCuentaResponse;
    private TarjetaResponse tarjetaResponse;

    @BeforeEach
    void setUp() {
        tarjetaTest = new Tarjeta();
        tarjetaTest.setGuid("921f6b86-695d-4361-8905-365d97691024");
        tarjetaTest.setNumeroTarjeta("4242424242424242");
        tarjetaTest.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaTest.setCvv(123);
        tarjetaTest.setPin("1234");
        tarjetaTest.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaTest.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaTest.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaTest.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        tipoCuentaTest = new TipoCuenta();
        tipoCuentaTest.setNombre("normal");
        tipoCuentaTest.setInteres(BigDecimal.valueOf(2.0));

        tipoCuentaResponse = new TipoCuentaResponse();
        tipoCuentaResponse.setGuid("hola");
        tipoCuentaResponse.setNombre("normal");
        tipoCuentaResponse.setInteres(BigDecimal.valueOf(2.0));

        tarjetaResponse = new TarjetaResponse();
        tarjetaResponse.setGuid("hola");
        tarjetaResponse.setNumeroTarjeta("4242424242424242");
        tarjetaResponse.setFechaCaducidad(LocalDate.parse("2025-12-31"));
        tarjetaResponse.setLimiteDiario(BigDecimal.valueOf(100.0));
        tarjetaResponse.setLimiteSemanal(BigDecimal.valueOf(200.0));
        tarjetaResponse.setLimiteMensual(BigDecimal.valueOf(500.0));
        tarjetaResponse.setTipoTarjeta(TipoTarjeta.valueOf("DEBITO"));

        cuentaTest = new Cuenta();
        cuentaTest.setGuid("12d45756-3895-49b2-90d3-c4a12d5ee081");
        cuentaTest.setIban("ES9120804243448487618583");
        cuentaTest.setSaldo(BigDecimal.valueOf(1000.0));
        cuentaTest.setTipoCuenta(tipoCuentaTest);
        cuentaTest.setTarjeta(tarjetaTest);
        cuentaTest.setIsDeleted(false);
    }

    @Test
    void toCuentaResponse() {
        var res = mapper.toCuentaResponse(cuentaTest, tipoCuentaResponse, tarjetaResponse);

        assertAll(
                () -> assertEquals(cuentaTest.getGuid(), res.getGuid()),
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
        cuentaRequest.setTipoCuentaId(tipoCuentaTest.getGuid());
        cuentaRequest.setTarjetaId(tarjetaTest.getGuid());

        var res = mapper.toCuenta(tipoCuentaTest, tarjetaTest);

        assertAll(
                () -> assertEquals(cuentaRequest.getTipoCuentaId(), res.getTipoCuenta().getGuid()),
                () -> assertEquals(cuentaRequest.getTarjetaId(), res.getTarjeta().getGuid()),
                () -> assertFalse(res.getIsDeleted())
        );
    }

    @Test
    void toCuentaUpdate() {
        CuentaRequestUpdate cuentaRequestUpdate = new CuentaRequestUpdate();
        cuentaRequestUpdate.setSaldo(BigDecimal.valueOf(1500.0));
        cuentaRequestUpdate.setTipoCuentaId(tipoCuentaTest.getGuid());
        cuentaRequestUpdate.setTarjetaId(tarjetaTest.getGuid());
        cuentaRequestUpdate.setIsDeleted(false);

        var res = mapper.toCuentaUpdate(cuentaRequestUpdate, cuentaTest, tipoCuentaTest, tarjetaTest);

        assertAll(
                () -> assertEquals(cuentaRequestUpdate.getSaldo(), res.getSaldo()),
                () -> assertEquals(cuentaRequestUpdate.getTipoCuentaId(), res.getTipoCuenta().getGuid()),
                () -> assertEquals(cuentaRequestUpdate.getTarjetaId(), res.getTarjeta().getGuid()),
                () -> assertFalse(res.getIsDeleted())
        );
    }
}