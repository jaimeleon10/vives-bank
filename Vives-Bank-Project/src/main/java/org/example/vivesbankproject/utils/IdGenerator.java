package org.example.vivesbankproject.utils;

import lombok.experimental.UtilityClass;
import java.security.SecureRandom;

@UtilityClass
public class IdGenerator {
    private static final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public String generarId() {
        StringBuilder id = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            int indice = random.nextInt(caracteres.length());
            id.append(caracteres.charAt(indice));
        }
        return id.toString();
    }
}
