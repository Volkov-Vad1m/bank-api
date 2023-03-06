package com.netcracker.edu.vavo.online.bank.security;

import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository repository;


    public AppUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOpt = repository.findByUsername(username);

        if(userOpt.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }

        UserEntity user = userOpt.get();

        return new User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());

    }
}
