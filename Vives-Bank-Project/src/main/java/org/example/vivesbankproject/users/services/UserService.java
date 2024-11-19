package org.example.vivesbankproject.users.services;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Page<UserResponse> getAll(Optional<String> username, Optional<Role> rol, Pageable pageable);

    UserResponse getById(UUID id);

    UserResponse getByUsername(String username);

    UserResponse save(UserRequest user);

    UserResponse update(UUID id, UserRequest user);

    void deleteById(UUID id);
}
