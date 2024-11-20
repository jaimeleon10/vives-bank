package org.example.vivesbankproject.users.repositories;

import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
                .id(UUID.randomUUID())
                .username("testuser")
                .password("password")
                .roles(Set.of(Role.USER))
                .build();

        userRepository.save(user);
    }

    @Test
    void FindByUsername() {
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void FindByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");
        assertThat(foundUser).isNotPresent();
    }

    @Test
    void SaveUser() {
        User newUser = User.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .password("newpassword")
                .roles(Set.of(Role.USER))
                .build();

        userRepository.save(newUser);

        Optional<User> foundUser = userRepository.findByUsername("newuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("newuser");
    }

    @Test
    void UpdateUser() {
        user.setUsername("updateduser");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("updateduser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("updateduser");
    }

    @Test
    void DeleteUser() {
        userRepository.delete(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isNotPresent();
    }
}
