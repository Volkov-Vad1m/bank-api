package com.netcracker.edu.vavo.online.bank.module.transaction;

import com.netcracker.edu.vavo.online.bank.module.transaction.model.ExternalTransactionForm;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.TransactionDTO;
import com.netcracker.edu.vavo.online.bank.module.transaction.model.InternalTransactionForm;

import java.util.List;

public interface TransactionService {

    List<TransactionDTO> getAllCurrentUserTransactions();

    List<TransactionDTO> internalTransfer(InternalTransactionForm form);

    public TransactionDTO externalTransfer(ExternalTransactionForm form);
}