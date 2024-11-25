package org.example.vivesbankproject.security.auth.services.users;

import org.example.vivesbankproject.security.auth.repositories.AuthUsersRepository;
import org.example.vivesbankproject.users.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service("userDetailsService")
public class AuthUsersServiceImpl implements AuthUsersService {

    private final AuthUsersRepository authUsersRepository;

    @Autowired
    public AuthUsersServiceImpl(AuthUsersRepository authUsersRepository) {
        this.authUsersRepository = authUsersRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username)  {
        return authUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException( username ));
    }
}