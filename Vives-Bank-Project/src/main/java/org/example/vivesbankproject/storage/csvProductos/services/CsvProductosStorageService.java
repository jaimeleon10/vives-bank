package org.example.vivesbankproject.storage.csvProductos.services;

import org.example.vivesbankproject.cuenta.models.TipoCuenta;
import org.example.vivesbankproject.cuenta.repositories.TipoCuentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CsvProductosStorageService {

    @Transactional
    List<TipoCuenta> importTiposCuentaFromCsv(MultipartFile file);

    TipoCuenta convertToTipoCuenta(String[] data);

    void init();

    String storeImportedCsv(MultipartFile file);

    void delete(String filename);
}
