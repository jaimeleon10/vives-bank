package org.example.vivesbankproject.users.services;

import org.example.vivesbankproject.users.dto.UserRequest;
import org.example.vivesbankproject.users.dto.UserResponse;
import org.example.vivesbankproject.users.exceptions.UserExists;
import org.example.vivesbankproject.users.exceptions.UserNotFound;
import org.example.vivesbankproject.users.mappers.UserMapper;
import org.example.vivesbankproject.users.models.Role;
import org.example.vivesbankproject.users.models.User;
import org.example.vivesbankproject.users.repositories.UserRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"usuario"})
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Page<User> getAll(Optional<String> username, Optional<Role> roles, Pageable pageable) {
        Specification<User> specUsername = (root, query, criteriaBuilder) ->
                username.map(u -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + u.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specRole = (root, query, criteriaBuilder) ->
                roles.map(r -> criteriaBuilder.equal(root.get("roles"), r))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.where(specUsername).and(specRole);

        return userRepository.findAll(criterio, pageable);
    }

    @Override
    @Cacheable(key = "#id")
    public UserResponse getById(UUID id) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Cacheable(key = "#username")
    public UserResponse getByUsername(String username) {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFound(username));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var user = userRepository.save(userMapper.toUser(userRequest));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse update(UUID id, UserRequest userRequest) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFound(id);
        }
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            throw new UserExists(userRequest.getUsername());
        }
        var user = userRepository.save(userMapper.toUser(userRequest));
        return userMapper.toUserResponse(user);
    }

    @Override
    public void deleteById(UUID id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new UserNotFound(id);
        }
        userRepository.deleteById(id);
    }
}
