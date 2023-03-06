package com.netcracker.edu.vavo.online.bank.module.user.model;

import com.netcracker.edu.vavo.online.bank.module.user.dao.UserEntity;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {

    private UUID id;
    private String username;
    private String name;

    public static UserDTO fromEntity(UserEntity entity) {
        UserDTO result = new UserDTO();
        result.setId(entity.getId());
        result.setUsername(entity.getUsername());
        result.setName(entity.getName());

        return result;
    }
}
