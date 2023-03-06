package com.netcracker.edu.vavo.online.bank.module.user.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreationForm {

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9.]{3,}$", message = "username must be consisted of min 3 symbols and began with letter")
    private String username;
    @NotBlank
    private String name;
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$", message = "password must contain at least 8 characters, including one uppercase and one number")
    private String password;

}
