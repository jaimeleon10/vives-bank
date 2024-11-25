package org.example.vivesbankproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableCaching
@EnableMongoRepositories(basePackages = {
        "org.example.vivesbankproject.movimientos.repositories",
        "org.example.vivesbankproject.movimientoTransaccion.repositories"
})
@EnableJpaRepositories(basePackages = {
        "org.example.vivesbankproject.cliente.repositories",
        "org.example.vivesbankproject.cuenta.repositories",
        "org.example.vivesbankproject.tarjeta.repositories",
        "org.example.vivesbankproject.users.repositories"
})
public class VivesBankProjectApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(VivesBankProjectApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n\n🕹️ SERVER IS RUNNING 🕹️\n\n");
    }
}
