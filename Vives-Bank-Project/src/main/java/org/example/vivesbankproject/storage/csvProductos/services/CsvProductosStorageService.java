package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.dto.tipoCuenta.TipoCuentaRequest;
import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CsvProductosStorageService {

    @Transactional
    List<TipoCuenta> importTiposCuentaFromCsv(MultipartFile file);

    TipoCuentaRequest convertToTipoCuentaRequest(String[] data);

    void init();

    String storeImportedCsv(MultipartFile file);

    void delete(String filename);
}
