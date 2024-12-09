package org.example.vivesbankproject.rest.users.repositories;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link User}. Extiende de {@link JpaRepository} para operaciones básicas
 * de persistencia y de {@link JpaSpecificationExecutor} para realizar búsquedas dinámicas con especificaciones.
 *
 * Proporciona métodos personalizados para buscar usuarios por identificador único o nombre de usuario.
 *
 * @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */
@Repository
@Schema(description = "Repositorio para interactuar con la base de datos de usuarios.")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Busca un usuario por su GUID único.
     *
     * @param guid Identificador global único del usuario.
     * @return Un {@link Optional} que contiene el usuario si se encuentra, en caso contrario estará vacío.
     */
    @Schema(description = "Busca un usuario en la base de datos utilizando su GUID único.")
    Optional<User> findByGuid(String guid);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario único.
     * @return Un {@link Optional} que contiene el usuario si se encuentra, en caso contrario estará vacío.
     */
    @Schema(description = "Busca un usuario en la base de datos utilizando su nombre de usuario.")
    Optional<User> findByUsername(String username);
}