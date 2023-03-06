package com.netcracker.edu.vavo.online.bank.module.account.rest;

import com.netcracker.edu.vavo.online.bank.module.account.AccountService;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountDTO;
import com.netcracker.edu.vavo.online.bank.module.account.model.AccountUpdateForm;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
public class AccountRestController {

    private final AccountService service;

    public AccountRestController(AccountService service) {
        this.service = service;
    }


    @GetMapping
    // убрать или для superuser
    public List<AccountDTO> findAllAccounts() {
        return service.findAllAccounts();
    }

    @GetMapping("/current")
    public AccountDTO getCurrentAccountInfo() {
        return service.getCurrentAccountInfo();
    }

    @PostMapping
    public AccountDTO createAccount() {
        return service.createAccount();
    }


}
