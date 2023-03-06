package com.netcracker.edu.vavo.online.bank.module.user.api;

import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserHelper {

    private final UserRepository repository;

    public UserHelper(UserRepository repository) {

        this.repository = repository;
    }

    public UserEntity getCurrentUserEntity() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((User) principal).getUsername();

        @SuppressWarnings("all")
                //to do get reference
        UserEntity entity = repository.findByUsername(username).get();
        return entity;
    }
}
