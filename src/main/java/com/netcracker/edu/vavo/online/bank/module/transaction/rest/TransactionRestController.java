package com.netcracker.edu.vavo.online.bank.module.transaction.rest;

import com.netcracker.edu.vavo.online.bank.module.transaction.TransactionService;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.ExternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionDTO;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.InternalTransactionForm;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionRestController {

    private final TransactionService service;


    public TransactionRestController(TransactionService service) {
        this.service = service;
    }



    @GetMapping
    public List<TransactionDTO> getAllCurrentUserTransactions() {
        return service.getAllCurrentUserTransactions();
    }

    @PostMapping("/internal")
    public TransactionDTO internalTransfer(@RequestBody InternalTransactionForm form) {
        return service.internalTransfer(form).get(0);
    }

    @PostMapping("/external")
    public TransactionDTO externalTransfer(@RequestBody ExternalTransactionForm form) {
        return service.externalTransfer(form);
    }

}
