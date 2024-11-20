package org.example.vivesbankproject.utils;

import lombok.experimental.UtilityClass;
import java.util.Random;

@UtilityClass
public class IdGenerator {
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public String generarIdYoutube(int longitud) {
        StringBuilder id = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int indice = RANDOM.nextInt(CARACTERES.length());
            id.append(CARACTERES.charAt(indice));
        }
        return id.toString();
    }
}
