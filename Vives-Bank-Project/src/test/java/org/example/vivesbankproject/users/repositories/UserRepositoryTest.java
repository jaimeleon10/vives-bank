package org.example.vivesbankproject.users.repositories;

import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user = User.builder()
                .guid("hola")
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        userRepository.save(user);
    }

    @Test
    void findByGuid() {
        Optional<User> foundUser = userRepository.findByGuid("hola");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getGuid()).isEqualTo("hola");
    }

    @Test
    void findByGuidNotFound() {
        Optional<User> foundUser = userRepository.findByGuid("nonexistent");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void findByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");
        assertThat(foundUser).isNotPresent();
    }
}