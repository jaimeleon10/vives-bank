package org.example.vivesbankproject.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración para la conexión y acceso a Redis.
 * Define la conexión, el cliente RedisTemplate y habilita repositorios Redis.
 *
 * <p>Esta configuración establece la conexión a un servidor Redis local en el puerto 6379,
 * configura el cliente para la serialización y deserialización de datos utilizando Jackson
 * para los valores y el nombre de clave como String para mejorar la compatibilidad con los tipos
 * de datos esperados en operaciones de almacenamiento.</p>
 *
 * @author Jaime León, Natalia González, German Fernandez, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Configuration
@EnableRedisRepositories
public class RedisConfiguration {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Configura la conexión con Redis utilizando la configuración por defecto en el puerto local 6379.
     *
     * @return JedisConnectionFactory que representa la conexión con el servidor Redis.
     */
    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);
        return new JedisConnectionFactory(configuration);
    }

    /**
     * Crea y configura un cliente RedisTemplate para operaciones de acceso a datos en Redis.
     * El cliente está configurado para:
     * - Usar un `StringRedisSerializer` para las claves.
     * - Utilizar `GenericJackson2JsonRedisSerializer` para serializar objetos JSON.
     * - Habilitar el soporte para transacciones.
     *
     * @return RedisTemplate configurado para realizar operaciones en Redis con soporte de transacciones.
     */
    @Bean
    public RedisTemplate<String, Object> template() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());

        // Configurar la serialización
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Soporte para operaciones transaccionales
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();

        return template;
    }
}