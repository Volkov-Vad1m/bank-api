package com.netcracker.edu.vavo.online.bank.module.user;

import com.netcracker.edu.vavo.online.bank.module.user.model.UserCreationForm;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> findAllUsers();
    UserDTO createNewUser(UserCreationForm form);
    UserDTO getCurrentUserInfo();

    UserDTO updateUser(UserCreationForm form);
}