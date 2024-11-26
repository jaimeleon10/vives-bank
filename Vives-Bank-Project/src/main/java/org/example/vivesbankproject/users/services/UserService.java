package org.example.vivesbankproject.users.services;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.models.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public interface UserService extends UserDetailsService{

    Page<UserResponse> getAll(Optional<String> username, Optional<Role> rol, Pageable pageable);

    UserResponse getById(String guid);

    UserResponse getByUsername(String username);

    UserResponse save(UserRequest user);

    UserResponse update(String guid, UserRequest user);

    void deleteById(String guid);

    @Override
    UserDetails loadUserByUsername(String username);

}
