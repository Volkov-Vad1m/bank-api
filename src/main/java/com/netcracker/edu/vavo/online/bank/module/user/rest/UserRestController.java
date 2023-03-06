package com.netcracker.edu.vavo.online.bank.module.user.rest;

import com.netcracker.edu.vavo.online.bank.module.user.UserService;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserCreationForm;
import com.netcracker.edu.vavo.online.bank.module.user.model.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
public class UserRestController {

    private final UserService service;

    public UserRestController(UserService service){
        this.service = service;
    }

    @GetMapping("/current")
    public UserDTO getCurrentUserInfo() {
        return service.getCurrentUserInfo();
    }

    @GetMapping
    public List<UserDTO> findAllUsers(){
        return service.findAllUsers();
    }

    @PostMapping
    public UserDTO createNewUser(@RequestBody UserCreationForm form) {
        return service.createNewUser(form);
    }

    @PostMapping("/update")
    public UserDTO updateUser(@RequestBody UserCreationForm form) {
        return service.updateUser(form);
    }

}
