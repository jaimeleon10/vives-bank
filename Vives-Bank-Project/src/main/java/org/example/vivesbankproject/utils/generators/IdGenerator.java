package org.example.vivesbankproject.utils.generators;

import lombok.experimental.UtilityClass;
import java.security.SecureRandom;

/**
 * Utility class para la generación de identificadores aleatorios.
 *
 * <p>Proporciona un método estático para crear un ID único compuesto por
 * caracteres alfanuméricos. Utiliza un generador de números aleatorios seguro
 * basado en {@link SecureRandom}.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@UtilityClass
public class IdGenerator {

    /**
     * Genera un identificador único alfanumérico de 11 caracteres.
     *
     * <p>El identificador se compone de letras (mayúsculas y minúsculas) y números,
     * utilizando un algoritmo que asegura aleatoriedad mediante {@link SecureRandom}.
     * La semilla del generador aleatorio se modifica usando el valor actual del sistema en nanosegundos
     * para mayor unicidad.</p>
     *
     * @return Un ID alfanumérico único de 11 caracteres.
     */
    public String generarId() {
        final String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final SecureRandom random = new SecureRandom();

        long nanos = System.nanoTime();
        random.setSeed(random.nextLong() ^ nanos); // Esto genera una semilla para modificar él random con los nanos

        StringBuilder id = new StringBuilder(11);
        for (int i = 0; i < 11; i++) {
            int indice = random.nextInt(caracteres.length());
            id.append(caracteres.charAt(indice));
        }
        return id.toString();
    }
}