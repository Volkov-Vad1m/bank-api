package com.netcracker.edu.vavo.online.bank.module.user.api;

import com.netcracker.edu.vavo.online.bank.exceptions.BusinessExceptionManager;
import com.netcracker.edu.vavo.online.bank.module.user.UserService;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import com.netcracker.edu.vavo.online.bank.module.user.dao.UserRepository;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserCreationForm;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final BusinessExceptionManager exceptionManager;
    private final PasswordEncoder passwordEncoder;
    private final UserHelper userHelper;
    private final Validator validator;


    public UserServiceImpl(UserRepository repository, BusinessExceptionManager exceptionManager,
                           PasswordEncoder passwordEncoder, UserHelper userHelper,
                           Validator validator) {

        this.repository = repository;
        this.exceptionManager = exceptionManager;
        this.passwordEncoder = passwordEncoder;
        this.userHelper = userHelper;
        this.validator = validator;
    }

    //__________________________________________
    @Override
    public List<UserDTO> findAllUsers() {
        return repository.findAll().stream().map(UserDTO::fromEntity).toList();
    }

    @Override
    public UserDTO createNewUser(UserCreationForm form) {
        validateUserCreationForm(form);

        String username = form.getUsername();
        validateUsernameOnConflict(username);

        UserEntity newUser = UserEntity.builder().
                id(UUID.randomUUID()).
                name(form.getName()).
                username(form.getUsername()).
                passwordHash(passwordEncoder.encode(form.getPassword())).
                build();

        return UserDTO.fromEntity(repository.save(newUser));
    }

    @Override
    public UserDTO getCurrentUserInfo() {
        return UserDTO.fromEntity(userHelper.getCurrentUserEntity());
    }

    public UserDTO updateUser(UserCreationForm form) {
        validateUserCreationForm(form);
        UserEntity user = userHelper.getCurrentUserEntity();

        if(!user.getUsername().equals(form.getUsername())) {
            validateUsernameOnConflict(form.getUsername());
        }

        user.setUsername(form.getUsername());
        user.setName(form.getName());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));

        return UserDTO.fromEntity(repository.save(user));
    }

    //__________________________________________
    //validation
    //__________________________________________

    private void validateUsernameOnConflict(String username) {
        if(repository.findByUsername(username).isPresent()) {
            exceptionManager.throwsException(
                    "ERR-001",
                    Map.of("username", username)
            );
        }
    }
    private void validateUserCreationForm(UserCreationForm form) {
        Set<ConstraintViolation<UserCreationForm>> validationResult = validator.validate(form);
        if(!validationResult.isEmpty()) {
            exceptionManager.throwsException("ERR-002", Map.of(
                    "constrains", validationResult.stream().collect(Collectors.toMap(
                            ConstraintViolation::getPropertyPath,
                            ConstraintViolation::getMessage
                    ))
            ));
        }
    }
}
