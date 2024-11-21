package org.example.vivesbankproject.utils;

import lombok.experimental.UtilityClass;
import org.example.vivesbankproject.cuenta.repositories.CuentaRepository;
import org.example.vivesbankproject.cuenta.services.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

@UtilityClass
public class IbanGenerator {

    public String generateIban() {
        String countryCode = "ES";
        String bankCode = "1234";
        String branchCode = "1234";
        String controlDigits = String.format("%02d", (int)(Math.random() * 100));
        String accountNumber = String.format("%010d", (int)(Math.random() * 1_000_000_0000L));

        String tempIban = bankCode + branchCode + controlDigits + accountNumber + "142800";

        String numericIban = tempIban.chars()
                .mapToObj(c -> Character.isDigit(c) ? String.valueOf((char) c) : String.valueOf(c - 'A' + 10))
                .reduce("", String::concat);

        int checksum = 98 - (new BigInteger(numericIban).mod(BigInteger.valueOf(97)).intValue());

        return countryCode + String.format("%02d", checksum) + bankCode + branchCode + controlDigits + accountNumber;
    }
}
