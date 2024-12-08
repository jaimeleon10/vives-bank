package org.example.vivesbankproject.rest.users.repositories;

import org.example.vivesbankproject.rest.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByGuid(String guid);
    Optional<User> findByUsername(String username);
}